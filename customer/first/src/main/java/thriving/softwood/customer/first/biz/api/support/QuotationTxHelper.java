package thriving.softwood.customer.first.biz.api.support;

import org.springframework.stereotype.Component;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;

import cn.hutool.v7.core.data.id.IdUtil;
import thriving.softwood.customer.first.biz.pojo.record.QuotationSubmitReq;
import thriving.softwood.customer.first.infrastructure.db.quotation.entity.base.Quotation;
import thriving.softwood.customer.first.infrastructure.db.quotation.repo.QuotationRepo;

/**
 * 专门处理报价数据库写入的事务助手 确保事务提交受限于上层 Service 的 Lock 保护伞内
 */
@Component
public class QuotationTxHelper {

    private final QuotationRepo quotationRepo;

    public QuotationTxHelper(QuotationRepo quotationRepo) {
        this.quotationRepo = quotationRepo;
    }

    @DSTransactional(rollbackFor = Exception.class)
    public void executeSubmitQuotation(String supplierCode, QuotationSubmitReq req) {
        // 1. 将当前供应商、当前商品的已有报价全部置为失效 (outdated = 1)
        quotationRepo.lambdaUpdate().eq(Quotation::getSupplierCode, supplierCode)
            .eq(Quotation::getProductCode, req.productCode()).eq(Quotation::getOutdated, 0).eq(Quotation::getDeleted, 0)
            .set(Quotation::getOutdated, 1).update();

        // 2. 插入本次的全新报价
        Quotation newQuote = new Quotation();
        newQuote.setQuotationNo("Q" + IdUtil.getSnowflakeNextIdStr());
        newQuote.setSupplierCode(supplierCode);
        newQuote.setProductCode(req.productCode());
        newQuote.setProductName(req.productName());
        newQuote.setSupplyPlace(req.supplyPlace());
        newQuote.setAvailableQty(req.availableQty());
        newQuote.setQuantityUnit(req.quantityUnit());
        newQuote.setQuotationType(req.quotationType());
        newQuote.setUnitPrice(req.unitPrice());
        newQuote.setTaxRate(req.taxRate());
        newQuote.setRemark(req.remark());
        newQuote.setAuxiliaryMaterial(req.auxiliaryMaterial());
        newQuote.setOutdated(0);
        newQuote.setDeleted(0);

        quotationRepo.save(newQuote);
    }

    @DSTransactional(rollbackFor = Exception.class)
    public void executeDeleteQuotation(String supplierCode, Long quotationId) {
        quotationRepo.lambdaUpdate().eq(Quotation::getId, quotationId).eq(Quotation::getSupplierCode, supplierCode)
            .set(Quotation::getDeleted, 1).set(Quotation::getOutdated, 1).update();
    }
}