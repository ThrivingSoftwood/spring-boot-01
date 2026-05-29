package thriving.softwood.customer.first.biz.controller;

import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.v7.core.text.StrUtil;
import jakarta.servlet.http.HttpServletResponse;
import thriving.softwood.common.core.result.Result;
import thriving.softwood.customer.first.biz.api.support.SupplierInfoSvc;
import thriving.softwood.customer.first.biz.pojo.record.ProvisionSupplierUserReq;
import thriving.softwood.customer.first.infrastructure.db.quotation.entity.base.SupplierInfo;
import thriving.softwood.customer.first.infrastructure.db.quotation.repo.SupplierInfoRepo;

@RestController
@RequestMapping("/customer/supplier")
public class SupplierInfoController {
    private final SupplierInfoRepo supplierInfoRepo;
    private final SupplierInfoSvc supplierInfoSvc;

    public SupplierInfoController(SupplierInfoRepo supplierInfoRepo, SupplierInfoSvc supplierInfoSvc) {
        this.supplierInfoRepo = supplierInfoRepo;
        this.supplierInfoSvc = supplierInfoSvc;
    }

    /**
     * 🚀 F-18: 导出 Excel (不需要统一 Result 包装，直接写出流)
     */
    @GetMapping("/export")
    public void exportSuppliers(HttpServletResponse response) {
        supplierInfoSvc.exportSuppliers(response);
    }

    /**
     * 🚀 F-01~F-04: 为供应商开通外部账号
     */
    @PostMapping("/provision-user")
    public Result<Void> provisionUser(@RequestBody ProvisionSupplierUserReq req) {
        supplierInfoSvc.provisionSupplierUser(req);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<Page<SupplierInfo>> page(@RequestParam(defaultValue = "1") long pageNo,
        @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<SupplierInfo> query = new LambdaQueryWrapper<>();
        query.eq(SupplierInfo::getDeleted, 0);
        if (StrUtil.isNotBlank(keyword)) {
            query.and(
                q -> q.like(SupplierInfo::getSupplierCode, keyword).or().like(SupplierInfo::getSupplierName, keyword));
        }
        query.orderByDesc(SupplierInfo::getCreateTime);

        return Result.success(supplierInfoRepo.page(new Page<>(pageNo, pageSize), query));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody SupplierInfo supplier) {
        supplierInfoRepo.save(supplier);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody SupplierInfo supplier) {
        supplierInfoRepo.updateById(supplier);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result<SupplierInfo> detail(@PathVariable Integer id) {
        return Result.success(supplierInfoRepo.getById(id));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        supplierInfoRepo.lambdaUpdate().eq(SupplierInfo::getId, id).set(SupplierInfo::getDeleted, 1).update();
        return Result.success();
    }
}