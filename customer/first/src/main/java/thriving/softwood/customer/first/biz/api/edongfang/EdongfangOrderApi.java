package thriving.softwood.customer.first.biz.api.edongfang;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import thriving.softwood.customer.first.biz.pojo.dto.EdongfangOrderDTO;
import thriving.softwood.customer.first.biz.pojo.record.EdongfangOrderQryReq;
import thriving.softwood.customer.first.biz.pojo.record.EdongfangOrderReq;
import thriving.softwood.customer.first.biz.pojo.vo.EdongfangOrderDetailVO;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangOrderItems;

public interface EdongfangOrderApi {
    /**
     * 取消订单 (状态: -2)
     */
    void cancelOrders(EdongfangOrderReq req);

    /**
     * 妥投完成 (状态: 1)
     */
    void deliverOrders(EdongfangOrderReq req);

    /**
     * 订单发货 (状态: 5)
     */
    void shipOrders(EdongfangOrderReq req);

    /**
     * 查询
     */
    Page<EdongfangOrderDTO> pageOrders(long pageNo, long pageSize, EdongfangOrderQryReq req);

    EdongfangOrderDetailVO getOrderDetail(String eOrderId);

    EdongfangOrderItems getOrderItemDetail(String pk);
}
