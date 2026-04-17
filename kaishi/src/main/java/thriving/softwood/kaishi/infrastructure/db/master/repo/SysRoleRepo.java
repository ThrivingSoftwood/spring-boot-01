package thriving.softwood.kaishi.infrastructure.db.master.repo;

import static thriving.softwood.kaishi.biz.constant.BaseConst.SUPER_ADMIN_ROLE;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysRole;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysRoleMapper;

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
public class SysRoleRepo extends AncestorServiceImpl<SysRoleMapper, SysRole> {

    public List<SysRole> listAll() {
        return lambdaQuery().eq(SysRole::getDeleted, 0).ne(SysRole::getRoleCode, SUPER_ADMIN_ROLE)
            .orderByAsc(SysRole::getSortOrder).list();
    }

    public Long countRoleCode(String roleCode) {
        return lambdaQuery().eq(SysRole::getRoleCode, roleCode).count();
    }

}
