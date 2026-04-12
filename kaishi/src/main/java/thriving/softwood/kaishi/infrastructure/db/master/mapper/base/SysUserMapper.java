package thriving.softwood.kaishi.infrastructure.db.master.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.kaishi.infrastructure.db.master.entity.SysUser;

/**
* <p>
    *  Mapper 接口
    * </p>
*
* @author meta-thriving
* @since 2026-04-08
*/
@DS("master")
public interface SysUserMapper extends BaseMapper<SysUser> {
}

