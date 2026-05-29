package thriving.softwood.customer.first.biz.controller;

import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.common.core.result.Result;
import thriving.softwood.customer.first.infrastructure.db.quotation.entity.base.ProductInfo;
import thriving.softwood.customer.first.infrastructure.db.quotation.repo.ProductInfoRepo;

/**
 * 📦 F-09: 商品信息管理
 */
@RestController
@RequestMapping("/customer/product")
public class ProductInfoController {

    private final ProductInfoRepo productInfoRepo;

    public ProductInfoController(ProductInfoRepo productInfoRepo) {
        this.productInfoRepo = productInfoRepo;
    }

    @GetMapping("/page")
    public Result<Page<ProductInfo>> page(@RequestParam(defaultValue = "1") long pageNo,
        @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<ProductInfo> query = new LambdaQueryWrapper<>();
        query.eq(ProductInfo::getDeleted, 0);
        if (StrUtil.isNotBlank(keyword)) {
            query
                .and(q -> q.like(ProductInfo::getProductCode, keyword).or().like(ProductInfo::getProductName, keyword));
        }
        query.orderByDesc(ProductInfo::getCreateTime);

        return Result.success(productInfoRepo.page(new Page<>(pageNo, pageSize), query));
    }

    @GetMapping("/detail/{id}")
    public Result<ProductInfo> detail(@PathVariable Integer id) {
        return Result.success(productInfoRepo.getById(id));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody ProductInfo product) {
        // 后续可追加校验 product_code 是否重复等逻辑
        productInfoRepo.save(product);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody ProductInfo product) {
        productInfoRepo.updateById(product);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        productInfoRepo.lambdaUpdate().eq(ProductInfo::getId, id).set(ProductInfo::getDeleted, 1).update();
        return Result.success();
    }
}