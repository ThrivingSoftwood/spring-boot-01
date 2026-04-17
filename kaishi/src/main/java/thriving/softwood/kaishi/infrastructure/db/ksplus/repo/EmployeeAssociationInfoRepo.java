package thriving.softwood.kaishi.infrastructure.db.ksplus.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.ksplus.entity.base.EmployeeAssociationInfo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.mapper.base.EmployeeAssociationInfoMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-16
 */
@DS("ksplus")
@Service
public class EmployeeAssociationInfoRepo
    extends AncestorServiceImpl<EmployeeAssociationInfoMapper, EmployeeAssociationInfo> {
    public List<EmployeeAssociationInfo> listAll() {
        return lambdaQuery().eq(EmployeeAssociationInfo::getDeleted, 0).list();
    }

    public Long countByEmployeeTypeId(String employeeTypeId) {
        return lambdaQuery().eq(EmployeeAssociationInfo::getEmployeeTypeid, employeeTypeId).count();
    }

    public void logicDelete(String loginAccount) {
        lambdaUpdate().eq(EmployeeAssociationInfo::getLoginAccount, loginAccount)
            .set(EmployeeAssociationInfo::getDeleted, 1).update();
    }
}
