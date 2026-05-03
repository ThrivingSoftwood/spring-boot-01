package thriving.softwood.kaishi.biz.api.edongfang;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangOrderQryReq;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangOrderReq;
import thriving.softwood.kaishi.biz.pojo.vo.EdongfangOrderDetailVO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrderItems;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrders;

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
    Page<EdongfangOrders> pageOrders(long pageNo, long pageSize, EdongfangOrderQryReq req);

    EdongfangOrderDetailVO getOrderDetail(String eOrderId);

    EdongfangOrderItems getOrderItemDetail(String pk);
}
