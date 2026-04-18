package thriving.softwood.kaishi.infrastructure.db.master.repo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysRoleDataRule;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysRoleDataRuleMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-15
 */
@DS("master")
@Service
public class SysRoleDataRuleRepo extends AncestorServiceImpl<SysRoleDataRuleMapper, SysRoleDataRule> {

    public void removeByRoleId(Long roleId) {
        lambdaUpdate().eq(SysRoleDataRule::getRoleId, roleId).remove();
    }

    public List<Long> listAssignedDataRuleIds(Long roleId) {
        return lambdaQuery().eq(SysRoleDataRule::getRoleId, roleId).list().stream().map(SysRoleDataRule::getRuleId)
            .collect(Collectors.toList());
    }

    public void addAll(List<SysRoleDataRule> list) {
        for (SysRoleDataRule sysRoleDataRule : list) {
            save(sysRoleDataRule);
        }
    }

    public void logicDeleteByRoleId(Long roleId) {
        lambdaUpdate().eq(SysRoleDataRule::getRoleId, roleId).remove();
    }

    public List<Long> listDataRuleIdsByRoleIds(List<Long> roleIds) {
        return lambdaQuery().in(SysRoleDataRule::getRoleId, roleIds).list().stream().map(SysRoleDataRule::getRuleId)
            .distinct().collect(Collectors.toList());
    }

    public List<Long> listRoleIdsByDataRuleId(Long dataRuleId) {
        return lambdaQuery().eq(SysRoleDataRule::getRuleId, dataRuleId).eq(SysRoleDataRule::getDeleted, 0)
            .orderByAsc(SysRoleDataRule::getRoleId).list().stream().map(SysRoleDataRule::getRoleId).distinct()
            .collect(Collectors.toList());
    }
}
