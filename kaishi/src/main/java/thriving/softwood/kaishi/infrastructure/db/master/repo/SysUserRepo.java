package thriving.softwood.kaishi.infrastructure.db.master.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.master.entity.SysUser;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysUserMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-08
 */
@DS("master")
@Service
public class SysUserRepo extends AncestorServiceImpl<SysUserMapper, SysUser> {
    public SysUser getByLoginAccount(String loginAccount) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>();
        queryWrapper.eq(SysUser::getLoginAccount, loginAccount);
        return getOne(queryWrapper);
    }
}
