package thriving.softwood.common.auth.pojo.record;

public record FinishPurchaseReq(Integer vchType, Long vchCode, Long dlyOrder, String extInfo) {
}