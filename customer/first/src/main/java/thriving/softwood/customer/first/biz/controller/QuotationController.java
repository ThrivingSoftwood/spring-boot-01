package thriving.softwood.customer.first.biz.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.customer.first.biz.api.quotation.QuotationSvc;
import thriving.softwood.customer.first.biz.pojo.record.QuotationSubmitReq;
import thriving.softwood.customer.first.biz.pojo.vo.QuotationRankVO;

@RestController
@RequestMapping("/customer/quotation")
public class QuotationController {

    private final QuotationSvc quotationSvc;

    public QuotationController(QuotationSvc quotationSvc) {
        this.quotationSvc = quotationSvc;
    }

    /**
     * 🚀 供应商发起/更新报价 (F-11, F-12, F-13)
     */
    @PostMapping("/submit")
    public Result<Void> submitQuotation(@RequestBody QuotationSubmitReq req) {
        quotationSvc.submitQuotation(req);
        return Result.success();
    }

    /**
     * 🚀 供应商撤销误发起的报价 (F-14)
     */
    @DeleteMapping("/delete/{id}/{productCode}")
    public Result<Void> deleteQuotation(@PathVariable Long id, @PathVariable String productCode) {
        quotationSvc.deleteQuotation(id, productCode);
        return Result.success();
    }

    /**
     * 🚀 F-16: 供应商查看“我的报价”
     */
    @GetMapping("/my-list")
    public Result<List<QuotationRankVO>> myQuotations() {
        return Result.success(quotationSvc.listMyQuotations());
    }

    /**
     * 🚀 F-17: 管理员/员工查看某商品的报价大盘
     */
    @GetMapping("/dashboard/{productCode}")
    public Result<List<QuotationRankVO>> dashboard(@PathVariable String productCode) {
        return Result.success(quotationSvc.listDashboard(productCode));
    }
}