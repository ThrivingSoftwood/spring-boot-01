package thriving.softwood.customer.first.infrastructure.db.ksplus.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.customer.first.infrastructure.db.ksplus.entity.base.EmployeeAssociationInfo;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-16
 */
@DS("ksplus")
public interface EmployeeAssociationInfoMapper extends BaseMapper<EmployeeAssociationInfo> {}
