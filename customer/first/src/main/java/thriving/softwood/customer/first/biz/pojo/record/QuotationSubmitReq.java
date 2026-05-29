package thriving.softwood.customer.first.biz.pojo.record;

import java.math.BigDecimal;

/**
 * 供应商提交报价请求体
 */
public record QuotationSubmitReq(String productCode, String productName, String supplyPlace, BigDecimal availableQty,
    String quantityUnit, String quotationType, BigDecimal unitPrice, BigDecimal taxRate, String remark,
    String auxiliaryMaterial) {
}