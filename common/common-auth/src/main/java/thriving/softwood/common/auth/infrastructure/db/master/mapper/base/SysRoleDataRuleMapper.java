package thriving.softwood.common.auth.infrastructure.db.master.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysRoleDataRule;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-17
 */
@DS("master")
public interface SysRoleDataRuleMapper extends BaseMapper<SysRoleDataRule> {}
