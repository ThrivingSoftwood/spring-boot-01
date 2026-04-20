package thriving.softwood.common.auth.controller.system;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import thriving.softwood.common.auth.api.system.DataRuleApi;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysDataRule;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysDataRuleRepo;
import thriving.softwood.common.auth.pojo.record.DataRuleReq;
import thriving.softwood.common.core.result.Result;

@RestController
@RequestMapping("/system/data-rule")
public class DataRuleController {

    private final SysDataRuleRepo dataRuleRepo;
    private final DataRuleApi dataRuleApi;

    public DataRuleController(SysDataRuleRepo dataRuleRepo, DataRuleApi dataRuleApi) {
        this.dataRuleRepo = dataRuleRepo;
        this.dataRuleApi = dataRuleApi;
    }

    /**
     * 获取所有可用的数据规则 (提供给角色分配弹窗使用)
     */
    @GetMapping("/all")
    public Result<List<SysDataRule>> getAllDataRules() {
        return Result.success(dataRuleRepo.listAll());
    }

    @PostMapping("/save")
    public Result<String> saveOrUpdate(@RequestBody DataRuleReq req) {
        dataRuleApi.saveOrUpdate(req);
        return Result.success("数据规则保存成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        dataRuleApi.logicDelete(id);
        return Result.success("数据规则已删除");
    }
}