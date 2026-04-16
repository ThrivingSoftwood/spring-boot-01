package thriving.softwood.kaishi.infrastructure.db.master.repo;

import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysUserRole;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysUserRoleMapper;
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
public class SysUserRoleRepo extends AncestorServiceImpl<SysUserRoleMapper, SysUserRole> {

}
