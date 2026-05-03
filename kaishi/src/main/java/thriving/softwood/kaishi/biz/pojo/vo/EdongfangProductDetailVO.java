package thriving.softwood.kaishi.biz.pojo.vo;

import java.util.List;

import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.*;

/**
 * 商品全景详情视图对象 包含商品主信息及所有一对多关联子表信息
 */
public record EdongfangProductDetailVO(EdongfangProducts product, List<EdongfangProductPrices> prices,
    List<EdongfangProductImages> images, List<EdongfangProductParams> params, List<EdongfangProductStocks> stocks) {
}