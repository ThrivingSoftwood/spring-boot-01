package thriving.softwood.customer.first.biz.api.purchase;

import java.util.List;

import thriving.softwood.common.auth.pojo.record.FinishPurchaseReq;
import thriving.softwood.customer.first.biz.pojo.dto.PurchaseOrderTraceDTO;
import thriving.softwood.customer.first.biz.pojo.vo.DlyBuyVO;
import thriving.softwood.customer.first.biz.pojo.vo.PurchaseTraceVO;

/**
 * @author ThrivingSoftwood
 */
public interface PurchaseOrderTraceApi {
    default List<DlyBuyVO> listTraceInfo(PurchaseOrderTraceDTO dto) {
        return List.of();
    }

    default List<PurchaseTraceVO> listTraceDetail(PurchaseOrderTraceDTO dto) {
        return List.of();
    }

    default void manualFinishPurchase(FinishPurchaseReq dto) {}
}
