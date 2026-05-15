package thriving.softwood.customer.first.biz.pojo.vo;

import java.util.List;

import thriving.softwood.customer.first.biz.pojo.dto.EdongfangOrderDTO;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangOrderItems;

/**
 * 订单全景详情视图对象 包含主订单信息及订单下的所有商品明细
 */
public record EdongfangOrderDetailVO(EdongfangOrderDTO order, List<EdongfangOrderItems> items) {
}