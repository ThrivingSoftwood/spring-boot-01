package thriving.softwood.kaishi.biz.controller;

import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.kaishi.biz.api.edongfang.EdongfangOrderApi;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangOrderQryReq;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangOrderReq;
import thriving.softwood.kaishi.biz.pojo.vo.EdongfangOrderDetailVO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrderItems;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrders;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangOrdersRepo;

@RestController
@RequestMapping("/kaishi/edongfang/order")
public class EdongfangOrderController {

    private final EdongfangOrderApi edongfangOrderApi;
    private final EdongfangOrdersRepo ordersRepo;

    public EdongfangOrderController(EdongfangOrderApi edongfangOrderApi, EdongfangOrdersRepo ordersRepo) {
        this.edongfangOrderApi = edongfangOrderApi;
        this.ordersRepo = ordersRepo;
    }

    /**
     * 🌟 分页查询订单 (支持 E采订单号逗号分割 IN 查询)
     */
    @GetMapping("/page")
    public Result<Page<EdongfangOrders>> pageOrders(@RequestParam(defaultValue = "1") long pageNo,
        @RequestParam(defaultValue = "50") long pageSize, EdongfangOrderQryReq req) { // 👈 移除 @RequestParam
        return Result.success(edongfangOrderApi.pageOrders(pageNo, pageSize, req));
    }

    /**
     * 2. 订单全景详情查询 (包含下属商品明细)
     */
    @GetMapping("/detail/{eOrderId}")
    public Result<EdongfangOrderDetailVO> getOrderDetail(@PathVariable String eOrderId) {
        return Result.success(edongfangOrderApi.getOrderDetail(eOrderId));
    }

    /**
     * 3. 订单明细单项详情查询
     */
    @GetMapping("/item-detail/{pk}")
    public Result<EdongfangOrderItems> getOrderItemDetail(@PathVariable String pk) {
        return Result.success(edongfangOrderApi.getOrderItemDetail(pk));
    }

    @PutMapping("/cancel")
    public Result<Void> cancelOrders(@RequestBody EdongfangOrderReq req) {
        edongfangOrderApi.cancelOrders(req);
        return Result.success();
    }

    @PutMapping("/deliver")
    public Result<Void> deliverOrders(@RequestBody EdongfangOrderReq req) {
        edongfangOrderApi.deliverOrders(req);
        return Result.success();
    }

    @PutMapping("/ship")
    public Result<Void> shipOrders(@RequestBody EdongfangOrderReq req) {
        edongfangOrderApi.shipOrders(req);
        return Result.success();
    }
}