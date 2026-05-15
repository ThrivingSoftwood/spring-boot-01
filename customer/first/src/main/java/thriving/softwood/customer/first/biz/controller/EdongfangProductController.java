package thriving.softwood.customer.first.biz.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.customer.first.biz.api.edongfang.EdongfangProductApi;
import thriving.softwood.customer.first.biz.pojo.record.EdongfangProductQryReq;
import thriving.softwood.customer.first.biz.pojo.record.EdongfangProductReq;
import thriving.softwood.customer.first.biz.pojo.vo.EdongfangProductDetailVO;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangProducts;

@RestController
@RequestMapping("/cust0001/edongfang/product")
public class EdongfangProductController {

    private final EdongfangProductApi edongfangProductApi;

    public EdongfangProductController(EdongfangProductApi edongfangProductApi) {
        this.edongfangProductApi = edongfangProductApi;
    }

    /**
     * 1. 商品分页查询
     */
    @RequestMapping("/page")
    public Result<Page<EdongfangProducts>> pageProducts(@RequestBody EdongfangProductQryReq qryReq) {
        return Result.success(edongfangProductApi.pageProducts(qryReq));
    }

    /**
     * 2. 商品全景详情查询 (包含价格、库存、图片、属性等子表)
     */
    @GetMapping("/detail/{sku}")
    public Result<EdongfangProductDetailVO> getProductDetail(@PathVariable String sku) {
        return Result.success(edongfangProductApi.getProductDetail(sku));
    }

    @PostMapping("/save")
    public Result<Void> addProduct(@RequestBody EdongfangProductReq req) {
        edongfangProductApi.addProduct(req);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> updateProduct(@RequestBody EdongfangProductReq req) {
        edongfangProductApi.updateProduct(req);
        return Result.success();
    }

    @PutMapping("/state/{state}")
    public Result<Void> changeState(@PathVariable Integer state, @RequestBody List<String> skus) {
        edongfangProductApi.changeState(skus, state);
        return Result.success();
    }
}