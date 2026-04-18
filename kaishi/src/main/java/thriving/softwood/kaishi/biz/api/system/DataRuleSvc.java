package thriving.softwood.kaishi.biz.api.system;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.hutool.v7.core.collection.CollUtil;
import thriving.softwood.common.core.exception.DetailException;
import thriving.softwood.kaishi.biz.pojo.record.DataRuleReq;
import thriving.softwood.kaishi.biz.pojo.vo.SysDataRuleVO;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDataRule;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysDataRuleRepo;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysRoleDataRuleRepo;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysRoleRepo;

@Service
public class DataRuleSvc implements DataRuleApi {

    private SysDataRuleRepo sysDataRuleRepo;
    private SysRoleDataRuleRepo sysRoleDataRuleRepo;
    private SysRoleRepo sysRoleRepo;

    public DataRuleSvc(SysDataRuleRepo sysDataRuleRepo, SysRoleDataRuleRepo sysRoleDataRuleRepo,
        SysRoleRepo sysRoleRepo) {
        this.sysDataRuleRepo = sysDataRuleRepo;
        this.sysRoleDataRuleRepo = sysRoleDataRuleRepo;
        this.sysRoleRepo = sysRoleRepo;
    }

    @Override
    public List<SysDataRuleVO> allDataRules() {
        return sysDataRuleRepo.listAll().stream().map(SysDataRuleVO::new).toList();
    }

    @Override
    public void saveOrUpdate(DataRuleReq req) {
        SysDataRule rule = new SysDataRule();
        rule.setId(req.id());
        rule.setTargetResource(req.targetResource());
        rule.setRuleName(req.ruleName());
        rule.setScopeType(req.scopeType() != null ? req.scopeType().byteValue() : 1);
        rule.setCustomSqlJson(req.customSqlJson());

        sysDataRuleRepo.saveOrUpdate(rule);
    }

    @Override
    public void logicDelete(Long dataRuleId) {
        List<Long> roleIds = sysRoleDataRuleRepo.listRoleIdsByDataRuleId(dataRuleId);
        if (CollUtil.isNotEmpty(roleIds)) {
            throw new DetailException("该规则目前仍被以下角色启用,请检查!\n" + sysRoleRepo.getSummaryInfo(roleIds));
        }
        sysDataRuleRepo.logicDeleteById(dataRuleId);
    }
}
