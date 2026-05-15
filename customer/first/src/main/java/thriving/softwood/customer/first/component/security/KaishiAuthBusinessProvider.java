// File: ./cust0001/src/main/java/thriving/softwood/cust0001/component/security/KaishiAuthBusinessProvider.java
package thriving.softwood.customer.first.component.security;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import thriving.softwood.common.auth.infrastructure.db.master.repo.SysUserRepo;
import thriving.softwood.common.auth.pojo.record.DepartmentReq;
import thriving.softwood.common.auth.spi.AuthBusinessProvider;
import thriving.softwood.customer.first.infrastructure.db.ksplus.entity.base.DepartmentAssociationInfo;
import thriving.softwood.customer.first.infrastructure.db.ksplus.repo.DepartmentAssociationInfoRepo;
import thriving.softwood.customer.first.infrastructure.db.ksplus.repo.EmployeeAssociationInfoRepo;

@Component
public class KaishiAuthBusinessProvider implements AuthBusinessProvider {

    private final SysUserRepo sysUserRepo;
    private final EmployeeAssociationInfoRepo empAssocRepo;
    private final DepartmentAssociationInfoRepo deptAssocRepo;

    public KaishiAuthBusinessProvider(SysUserRepo sysUserrepo, EmployeeAssociationInfoRepo empAssocRepo,
        DepartmentAssociationInfoRepo deptAssocRepo) {
        sysUserRepo = sysUserrepo;
        this.empAssocRepo = empAssocRepo;
        this.deptAssocRepo = deptAssocRepo;
    }

    @Override
    public String getEmployeeTypeId(String loginAccount) {
        return empAssocRepo.getTypeIdByLoginAccount(loginAccount);
    }

    @Override
    public String getDepartmentTypeId(Long deptId) {
        if (deptId == null) {
            return null;
        }
        return deptAssocRepo.getTypeIdByDeptId(deptId);
    }

    @Override
    public void associateDelete(Long userId) {
        empAssocRepo.logicDelete(sysUserRepo.getById(userId).getLoginAccount());
    }

    @Override
    public void associateDelete(DepartmentReq req) {
        deptAssocRepo.logicDelete(req.id());
    }

    @Override
    public Map<Long, String> loadAssocDeptIds() {
        return deptAssocRepo.listAll().stream().collect(Collectors.toMap(DepartmentAssociationInfo::getAuthDepartmentId,
            DepartmentAssociationInfo::getOriDepartmentTypeid, (oldValue, newValue) -> newValue));
    }
}