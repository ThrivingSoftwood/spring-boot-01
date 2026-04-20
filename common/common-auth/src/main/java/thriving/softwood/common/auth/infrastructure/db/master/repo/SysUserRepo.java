package thriving.softwood.common.auth.infrastructure.db.master.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.common.auth.infrastructure.db.master.mapper.base.SysUserMapper;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;

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
public class SysUserRepo extends AncestorServiceImpl<SysUserMapper, SysUser> {
    public SysUser getByLoginAccount(String loginAccount) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>();
        queryWrapper.eq(SysUser::getLoginAccount, loginAccount);
        return getOne(queryWrapper);
    }

    public Long countUsers(Long deptId) {
        return lambdaQuery().eq(SysUser::getDeptId, deptId).count();
    }

    public List<SysUser> listAll() {
        return lambdaQuery().ne(SysUser::getLoginAccount, "kaishi").orderByAsc(SysUser::getId).list();
    }

    public void logicDelete(Long id) {
        lambdaUpdate().eq(SysUser::getId, id).set(SysUser::getDeleted, 1).update();
    }

    public List<SysUser> listByDeptIds(List<Long> affectedDeptIds) {
        return lambdaQuery().in(SysUser::getDeptId, affectedDeptIds).list();
    }

    public void updatePermissionVersionsByUserIds(String newVersion, List<Long> userIds) {
        lambdaUpdate().set(SysUser::getPermissionVersion, newVersion).in(SysUser::getId, userIds).update();
    }
}
