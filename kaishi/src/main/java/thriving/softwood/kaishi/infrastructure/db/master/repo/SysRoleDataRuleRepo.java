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

}
