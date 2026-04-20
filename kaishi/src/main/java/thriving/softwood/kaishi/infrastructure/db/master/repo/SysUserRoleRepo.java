package thriving.softwood.kaishi.infrastructure.db.master.repo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysUserRole;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysUserRoleMapper;

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
public class SysUserRoleRepo extends AncestorServiceImpl<SysUserRoleMapper, SysUserRole> {

    public void logicDeleteByRoleId(Long roleId) {
        lambdaUpdate().eq(SysUserRole::getRoleId, roleId).set(SysUserRole::getDeleted, 1).update();
    }

    public void logicDeleteByUserId(Long userId) {
        lambdaUpdate().eq(SysUserRole::getUserId, userId).set(SysUserRole::getDeleted, 1).update();
    }

    public List<Long> listAssignedUserIdsByRoleId(Long roleId) {
        return lambdaQuery().eq(SysUserRole::getRoleId, roleId).list().stream().map(SysUserRole::getUserId)
            .collect(Collectors.toList());
    }

    public void addAll(List<SysUserRole> sysUserRoles) {
        for (SysUserRole sysUserRole : sysUserRoles) {
            save(sysUserRole);
        }
    }

    public List<Long> listRoleIdsByUserId(Long userId) {
        return lambdaQuery().eq(SysUserRole::getUserId, userId).list().stream().map(SysUserRole::getRoleId)
            .collect(Collectors.toList());
    }
}
