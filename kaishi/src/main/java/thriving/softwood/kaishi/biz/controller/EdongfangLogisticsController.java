package thriving.softwood.kaishi.biz.controller;

import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.kaishi.biz.api.edongfang.EdongfangLogisticsApi;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangLogisticQryReq;
import thriving.softwood.kaishi.biz.pojo.vo.EdongfangLogisticsDetailVO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangLogistics;

/**
 * 物流发货管理模块入口
 */
@RestController
@RequestMapping("/kaishi/edongfang/logistics")
public class EdongfangLogisticsController {

    private final EdongfangLogisticsApi logisticsApi;

    public EdongfangLogisticsController(EdongfangLogisticsApi logisticsApi) {
        this.logisticsApi = logisticsApi;
    }

    @PostMapping("/save")
    public Result<Void> addLogistics(@RequestBody EdongfangLogistics logistics) {
        logisticsApi.addLogistics(logistics);
        return Result.success();
    }

    /**
     * 1. 发货已确认信息分页列表
     */
    @GetMapping("/page")
    public Result<Page<EdongfangLogistics>> pageLogistics(@RequestParam(defaultValue = "1") long pageNo,
        @RequestParam(defaultValue = "20") long pageSize, EdongfangLogisticQryReq logisticsReq) { // 👈 移除 @RequestParam
        return Result.success(logisticsApi.pageLogistics(pageNo, pageSize, logisticsReq));
    }

    /**
     * 2. 发货信息详情查询 (全景)
     * 
     * @param pk edongfang_logistics 表的主键
     */
    @GetMapping("/detail/{pk}")
    public Result<EdongfangLogisticsDetailVO> getLogisticsDetail(@PathVariable String pk) {
        return Result.success(logisticsApi.getLogisticsDetail(pk));
    }

    /**
     * 3. 🌟 新增：修改发货已确认主信息 接收前端传来的物流对象，根据主键 pk 进行更新
     */
    @PutMapping("/update")
    public Result<Void> updateLogistics(@RequestBody EdongfangLogistics logistics) {
        logisticsApi.updateLogistics(logistics);
        return Result.success();
    }
}