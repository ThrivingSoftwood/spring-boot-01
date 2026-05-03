package thriving.softwood.kaishi.biz.pojo.vo;

import java.util.List;

import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrderItems;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrders;

/**
 * 订单全景详情视图对象 包含主订单信息及订单下的所有商品明细
 */
public record EdongfangOrderDetailVO(EdongfangOrders order, List<EdongfangOrderItems> items) {
}