package thriving.softwood.common.auth.infrastructure.db.master.mapper.extend;

import java.util.List;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysPermission;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-15
 */
@DS("master")
public interface SysPermissionExtendMapper extends BaseMapper<SysPermission> {
    List<SysPermission> listMenuPermissionsByAccount(String loginAccount);
}
