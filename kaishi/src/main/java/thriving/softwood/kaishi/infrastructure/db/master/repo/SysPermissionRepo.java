package thriving.softwood.kaishi.infrastructure.db.master.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysPermission;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysPermissionMapper;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.extend.SysPermissionExtendMapper;

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
public class SysPermissionRepo extends AncestorServiceImpl<SysPermissionMapper, SysPermission> {
    private SysPermissionExtendMapper extendMapper;

    @Autowired
    public SysPermissionRepo(SysPermissionExtendMapper extendMapper) {
        this.extendMapper = extendMapper;
    }

    public List<SysPermission> listSorted() {
        return lambdaQuery().orderByAsc(SysPermission::getParentId, SysPermission::getSortOrder).list();
    }

    public Long countSubNodes(Long id) {
        return lambdaQuery().eq(SysPermission::getParentId, id).count();
    }

    public List<SysPermission> listMenuPermissionsByAccount(String loginAccount) {
        return extendMapper.listMenuPermissionsByAccount(loginAccount);
    }
}
