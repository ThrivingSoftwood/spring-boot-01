package thriving.softwood.customer.first.biz.pojo.record;

import java.math.BigDecimal;

/**
 * 商品全量保存/修改聚合请求体
 */
public record EdongfangProductQryReq(Long pageNo, Long pageSize, String sku, String name, String productArea,
    BigDecimal minWeight, BigDecimal maxWeight) {
}