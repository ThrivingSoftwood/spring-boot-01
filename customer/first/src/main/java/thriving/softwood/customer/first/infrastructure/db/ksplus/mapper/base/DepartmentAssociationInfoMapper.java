package thriving.softwood.customer.first.infrastructure.db.ksplus.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.customer.first.infrastructure.db.ksplus.entity.base.DepartmentAssociationInfo;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-16
 */
@DS("ksplus")
public interface DepartmentAssociationInfoMapper extends BaseMapper<DepartmentAssociationInfo> {}
