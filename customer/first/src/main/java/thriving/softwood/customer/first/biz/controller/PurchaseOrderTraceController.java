package thriving.softwood.customer.first.biz.controller;

import static thriving.softwood.common.core.enums.RespCodeEnum.INTERNAL_SERVER_ERROR;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thriving.softwood.common.auth.pojo.record.FinishPurchaseReq;
import thriving.softwood.common.core.result.Result;
import thriving.softwood.customer.first.biz.api.purchase.PurchaseOrderTraceApi;
import thriving.softwood.customer.first.biz.pojo.dto.PurchaseOrderTraceDTO;
import thriving.softwood.customer.first.biz.pojo.vo.DlyBuyVO;
import thriving.softwood.customer.first.biz.pojo.vo.PurchaseTraceVO;

/**
 * @author ThrivingSoftwood
 */
@RestController
@RequestMapping("/cust0001/purchase/trace")
public class PurchaseOrderTraceController {
    private final PurchaseOrderTraceApi api;

    @Autowired
    public PurchaseOrderTraceController(PurchaseOrderTraceApi api) {
        this.api = api;
    }

    @RequestMapping("/list/info")
    public Result<List<DlyBuyVO>> listTraceInfo(@RequestBody PurchaseOrderTraceDTO dto) {
        try {
            return Result.success(api.listTraceInfo(dto));
        } catch (Exception e) {
            return Result.error(INTERNAL_SERVER_ERROR.code(), e.getCause().getMessage());
        }
    }

    @RequestMapping("/list/detail")
    public Result<List<PurchaseTraceVO>> listTraceDetail(@RequestBody PurchaseOrderTraceDTO dto) {
        try {
            return Result.success(api.listTraceDetail(dto));
        } catch (Exception e) {
            return Result.error(INTERNAL_SERVER_ERROR.code(), e.getCause().getMessage());
        }
    }

    @RequestMapping("/manual/finish")
    public Result manualFinishPurchase(@RequestBody FinishPurchaseReq dto) {
        try {
            api.manualFinishPurchase(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(INTERNAL_SERVER_ERROR.code(), e.getCause().getMessage());
        }
    }
}
