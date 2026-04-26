package thriving.softwood.common.auth.infrastructure.db.master.mapper.extend;

import java.util.List;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysDept;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-17
 */
@DS("master")
public interface SysDeptExtendMapper extends BaseMapper<SysDept> {
    List<SysDept> listDeptWithChildren(Long deptId);
}
