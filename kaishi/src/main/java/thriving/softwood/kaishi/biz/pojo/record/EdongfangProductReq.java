package thriving.softwood.kaishi.biz.pojo.record;

import java.util.List;

import thriving.softwood.kaishi.biz.pojo.dto.EdongfangProductImageDTO;
import thriving.softwood.kaishi.biz.pojo.dto.EdongfangProductParamDTO;
import thriving.softwood.kaishi.biz.pojo.dto.EdongfangProductPriceDTO;
import thriving.softwood.kaishi.biz.pojo.dto.EdongfangProductStockDTO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangProducts;

/**
 * 商品全量保存/修改聚合请求体
 */
public record EdongfangProductReq(EdongfangProducts product, List<EdongfangProductPriceDTO> prices,
    List<EdongfangProductImageDTO> images, List<EdongfangProductParamDTO> params,
    List<EdongfangProductStockDTO> stocks) {
}