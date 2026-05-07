package thriving.softwood.kaishi.biz.pojo.vo;

import java.util.List;

import thriving.softwood.kaishi.biz.pojo.dto.EdongfangOrderDTO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrderItems;

/**
 * 订单全景详情视图对象 包含主订单信息及订单下的所有商品明细
 */
public record EdongfangOrderDetailVO(EdongfangOrderDTO order, List<EdongfangOrderItems> items) {
}