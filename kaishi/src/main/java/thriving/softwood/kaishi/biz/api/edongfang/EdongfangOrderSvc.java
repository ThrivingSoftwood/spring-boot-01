package thriving.softwood.kaishi.biz.api.edongfang;

import static thriving.softwood.kaishi.biz.enums.EdongfangOrderStatusEnum.*;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.map.MapUtil;
import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.json.JSONUtil;
import thriving.softwood.kaishi.biz.api.support.DictionaryApi;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangOrderQryReq;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangOrderReq;
import thriving.softwood.kaishi.biz.pojo.vo.EdongfangOrderDetailVO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangMessages;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrderItems;
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
        pushOrderStateMessage(req, CANCELED.code());
    }

    /**
     * 妥投完成 (状态: 1)
     */
    @Override
    public void deliverOrders(EdongfangOrderReq req) {
        pushOrderStateMessage(req, SIGNED.code());
    }

    /**
     * 订单发货 (状态: 5)
     */
    @Override
    public void shipOrders(EdongfangOrderReq req) {
        pushOrderStateMessage(req, SHIPPED.code());
        edongfangOrderStubRepo.shipOrders(req.eOrderIds());
    }

    /**
     * 核心推送逻辑
     */
    private void pushOrderStateMessage(EdongfangOrderReq req, Integer state) {
        for (String eOrderId : req.eOrderIds()) {
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
    public Page<EdongfangOrders> pageOrders(long pageNo, long pageSize, EdongfangOrderQryReq req) {
        var queryWrapper = ordersRepo.lambdaQuery();

        // 🌟 支持逗号分隔的 E采平台订单号 IN 查询
        if (StrUtil.isNotBlank(req.eOrderIds())) {
            List<String> idList = Arrays.asList(req.eOrderIds().split(","));
            queryWrapper.in(EdongfangOrders::getEOrderId, idList);
        }

        // 🌟 需求 2.3.9：发货信息页面复用（增加条件 status not in ('0','-2')）
        if (Boolean.TRUE.equals(req.queryShipped())) {
            queryWrapper.exists("select 1 from edongfang_order_stub as eos"
                + " where eos.e_order_id = edongfang_orders.e_order_id and eos.shipped_flag = {0}", 1);
        } else if (req.status() != null) {
            queryWrapper.eq(EdongfangOrders::getStatus, req.status());
        }

        queryWrapper.orderByDesc(EdongfangOrders::getCreateTime);
        return queryWrapper.page(new Page<>(pageNo, pageSize));
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
        return new EdongfangOrderDetailVO(order, items);
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