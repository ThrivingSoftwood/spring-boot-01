package thriving.softwood.customer.first.infrastructure.db.ksplus.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.customer.first.infrastructure.db.ksplus.entity.base.DepartmentAssociationInfo;
import thriving.softwood.customer.first.infrastructure.db.ksplus.mapper.base.DepartmentAssociationInfoMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-16
 */
@DS("ksplus")
@Service
public class DepartmentAssociationInfoRepo
    extends AncestorServiceImpl<DepartmentAssociationInfoMapper, DepartmentAssociationInfo> {
    public List<DepartmentAssociationInfo> listAll() {
        return lambdaQuery().list();
    }

    public void logicDelete(Long authDepartmentId) {
        lambdaUpdate().eq(DepartmentAssociationInfo::getAuthDepartmentId, authDepartmentId)
            .eq(DepartmentAssociationInfo::getDeleted, 0).set(DepartmentAssociationInfo::getDeleted, 1).update();
    }

    public String getTypeIdByDeptId(Long deptId) {
        return lambdaQuery().eq(DepartmentAssociationInfo::getAuthDepartmentId, deptId).one().getOriDepartmentTypeid();
    }
}
