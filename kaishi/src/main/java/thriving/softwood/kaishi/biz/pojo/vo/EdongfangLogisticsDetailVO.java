package thriving.softwood.kaishi.biz.pojo.vo;

import java.util.List;

import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangLogistics;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangLogisticsItems;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangLogisticsTracks;

/**
 * 物流发货全景详情视图对象 包含物流主表信息、包裹商品明细、物流运转轨迹
 */
public record EdongfangLogisticsDetailVO(EdongfangLogistics logistics, List<EdongfangLogisticsItems> items,
    List<EdongfangLogisticsTracks> tracks) {
}