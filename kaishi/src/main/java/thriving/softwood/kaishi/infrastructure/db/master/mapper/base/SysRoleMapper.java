package thriving.softwood.kaishi.infrastructure.db.master.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysRole;

/**
* <p>
    *  Mapper 接口
    * </p>
*
* @author meta-thriving
* @since 2026-04-15
*/
@DS("master")
public interface SysRoleMapper extends BaseMapper<SysRole> {
}

