package thriving.softwood.common.auth.infrastructure.db.master.repo;

import static thriving.softwood.common.auth.constant.BaseConst.SUPER_ADMIN_ROLE;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysRole;
import thriving.softwood.common.auth.infrastructure.db.master.mapper.base.SysRoleMapper;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-15
 */
@DS("master")
@Service
public class SysRoleRepo extends AncestorServiceImpl<SysRoleMapper, SysRole> {

    public List<SysRole> listAll() {
        return lambdaQuery().ne(SysRole::getRoleCode, SUPER_ADMIN_ROLE).orderByAsc(SysRole::getSortOrder).list();
    }

    public Long countRoleCode(String roleCode) {
        return lambdaQuery().eq(SysRole::getRoleCode, roleCode).count();
    }

    public void logicDeleteById(Long id) {
        lambdaUpdate().eq(SysRole::getId, id).set(SysRole::getDeleted, 1).update();
    }

    public String getSummaryInfo(List<Long> roleIds) {
        return listByIds(roleIds).stream().map(role -> role.getRoleName() + ":" + role.getRoleCode())
            .collect(Collectors.joining("\n"));
    }

    public List<SysRole> listActiveRoleByIds(List<Long> roleIds) {
        return lambdaQuery().in(SysRole::getId, roleIds).eq(SysRole::getStatus, 1).list();
    }
}
