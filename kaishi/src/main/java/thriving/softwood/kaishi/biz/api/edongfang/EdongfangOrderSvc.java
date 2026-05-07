package thriving.softwood.kaishi.biz.api.edongfang;

import static thriving.softwood.kaishi.biz.enums.EdongfangOrderStatusEnum.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.map.MapUtil;
import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.json.JSONUtil;
import thriving.softwood.kaishi.biz.api.support.DictionaryApi;
import thriving.softwood.kaishi.biz.pojo.dto.EdongfangOrderDTO;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangOrderQryReq;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangOrderReq;
import thriving.softwood.kaishi.biz.pojo.vo.EdongfangOrderDetailVO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangMessages;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrderItems;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrderStub;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrders;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangMessagesRepo;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangOrderItemsRepo;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangOrderStubRepo;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangOrdersRepo;

@Service
@DS("edongfang")
public class EdongfangOrderSvc implements EdongfangOrderApi {

    private final EdongfangMessagesRepo messagesRepo;
    private final EdongfangOrdersRepo ordersRepo;
    private final EdongfangOrderItemsRepo orderItemsRepo;
    private final EdongfangOrderStubRepo edongfangOrderStubRepo;
    private final DictionaryApi dict;

    public EdongfangOrderSvc(EdongfangMessagesRepo messagesRepo, EdongfangOrdersRepo ordersRepo,
        EdongfangOrderItemsRepo orderItemsRepo, EdongfangOrderStubRepo edongfangOrderStubRepo, DictionaryApi dict) {
        this.messagesRepo = messagesRepo;
        this.ordersRepo = ordersRepo;
        this.orderItemsRepo = orderItemsRepo;
        this.edongfangOrderStubRepo = edongfangOrderStubRepo;
        this.dict = dict;
    }

    /**
     * 取消订单 (状态: -2)
     */
    @Override
    public void cancelOrders(EdongfangOrderReq req) {
        ordersRepo.lambdaUpdate().in(EdongfangOrders::getEOrderId, req.eOrderIds())
            .set(EdongfangOrders::getStatus, CANCELED.code()).update();
        pushOrderStateMessage(req, CANCELED.code());
    }

    /**
     * 妥投完成 (状态: 1)
     */
    @Override
    public void deliverOrders(EdongfangOrderReq req) {
        ordersRepo.lambdaUpdate().in(EdongfangOrders::getEOrderId, req.eOrderIds())
            .set(EdongfangOrders::getStatus, SIGNED.code()).update();
        pushOrderStateMessage(req, SIGNED.code());
    }

    /**
     * 订单发货 (状态: 5)
     */
    @Override
    public void shipOrders(EdongfangOrderReq req) {
        ordersRepo.lambdaUpdate().in(EdongfangOrders::getEOrderId, req.eOrderIds())
            .set(EdongfangOrders::getStatus, SHIPPED.code()).update();
        pushOrderStateMessage(req, SHIPPED.code());
        edongfangOrderStubRepo.shipOrders(req.eOrderIds());
    }

    /**
     * 核心推送逻辑
     */
    private void pushOrderStateMessage(EdongfangOrderReq req, Integer state) {

        List<String> eOrderIds = req.eOrderIds();
        if (eOrderIds == null || eOrderIds.isEmpty()) {
            return;
        }

        for (String eOrderId : eOrderIds) {

            // 3. 构建并保存消息
            EdongfangMessages msg = new EdongfangMessages();
            msg.setPk(IdUtil.fastSimpleUUID());
            msg.setType("302");
            msg.setResult(JSONUtil.toJsonStr(MapUtil.ofKvs(false, "orderId", eOrderId, "state", state)));
            if (SHIPPED.code().equals(state)) {
                msg.setConsumed(0);
                msg.setDeleted(0);
            }
            messagesRepo.save(msg);
        }
    }

    @Override
    public Page<EdongfangOrderDTO> pageOrders(long pageNo, long pageSize, EdongfangOrderQryReq req) {
        return ordersRepo.page(pageNo, pageSize, req);
    }

    @Override
    public EdongfangOrderDetailVO getOrderDetail(String eOrderId) {
        EdongfangOrders order = ordersRepo.lambdaQuery().eq(EdongfangOrders::getEOrderId, eOrderId).one();
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 使用 parent(pk) 走索引查询明细
        List<EdongfangOrderItems> items =
            orderItemsRepo.lambdaQuery().eq(EdongfangOrderItems::getParent, order.getPk()).list();
        items.forEach(item -> {
            if (StrUtil.isNotBlank(item.getName())) {
                return;
            }
            item.setName(dict.getEdongfangProductName(item.getSku()));
        });
        EdongfangOrderStub stub =
            edongfangOrderStubRepo.lambdaQuery().eq(EdongfangOrderStub::getEOrderId, eOrderId).one();
        return new EdongfangOrderDetailVO(new EdongfangOrderDTO(order, stub), items);
    }

    @Override
    public EdongfangOrderItems getOrderItemDetail(String pk) {
        EdongfangOrderItems item = orderItemsRepo.getById(pk);
        if (StrUtil.isBlank(item.getName())) {
            item.setName(dict.getEdongfangProductName(item.getSku()));
        }
        return item;
    }
}