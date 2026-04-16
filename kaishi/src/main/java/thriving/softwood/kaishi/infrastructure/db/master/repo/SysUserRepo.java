package thriving.softwood.kaishi.infrastructure.db.master.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysUserMapper;

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
        return lambdaQuery().eq(SysUser::getDeptId, deptId).eq(SysUser::getDeleted, 0).count();
    }

}
