package thriving.softwood.kaishi.infrastructure.db.master.repo;

import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysRolePermission;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysRolePermissionMapper;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.dynamic.datasource.annotation.DS;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author meta-thriving
* @since 2026-04-15
*/
@DS("master")
@Service
public class SysRolePermissionRepo extends AncestorServiceImpl<SysRolePermissionMapper, SysRolePermission> {

}
