package thriving.softwood.kaishi.infrastructure.db.master.repo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysRolePermission;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysRolePermissionMapper;

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
public class SysRolePermissionRepo extends AncestorServiceImpl<SysRolePermissionMapper, SysRolePermission> {

    public void removeByRoleId(Long roleId) {
        lambdaUpdate().eq(SysRolePermission::getRoleId, roleId).remove();
    }

    public List<Long> listAssignedPermissionIdsByRoleId(Long roleId) {
        return lambdaQuery().eq(SysRolePermission::getRoleId, roleId).list().stream()
            .map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }

    public void addAll(List<SysRolePermission> list) {
        for (SysRolePermission sysRolePermission : list) {
            save(sysRolePermission);
        }
    }

    public void logicDeleteByRoleId(Long roleId) {
        lambdaUpdate().eq(SysRolePermission::getRoleId, roleId).remove();
    }

    public List<Long> listPermissionIdsByRoleIds(List<Long> roleIds) {
        return lambdaQuery().in(SysRolePermission::getRoleId, roleIds).list().stream()
            .map(SysRolePermission::getPermissionId).distinct().collect(Collectors.toList());
    }
}
