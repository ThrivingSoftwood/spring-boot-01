package thriving.softwood.customer.first.biz.pojo.record;

public record SupplierInfoReq(Integer id, String supplierCode, String supplierName, String region, String contactName,
    String contactPhone, String email, String invoiceName, String taxId) {
}