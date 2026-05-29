package thriving.softwood.kaishi.biz.api.provider;

import org.springframework.stereotype.Service;

import thriving.softwood.common.security.pojo.vo.SysDeptVO;
import thriving.softwood.common.security.spi.AuthAssociationProvider;
import thriving.softwood.kaishi.biz.pojo.vo.KaishiSysDeptVO;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.DepartmentAssociationInfoRepo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.EmployeeAssociationInfoRepo;

@Service
public class KaishiAuthAssocProvider implements AuthAssociationProvider {

    private final DepartmentAssociationInfoRepo deptAssocRepo;
    private final EmployeeAssociationInfoRepo empAssocRepo;

    public KaishiAuthAssocProvider(DepartmentAssociationInfoRepo deptAssocRepo,
        EmployeeAssociationInfoRepo empAssocRepo) {
        this.deptAssocRepo = deptAssocRepo;
        this.empAssocRepo = empAssocRepo;
    }

    @Override
    public void associateDelete(Long deptId) {
        deptAssocRepo.logicDelete(deptId);
    }

    @Override
    public SysDeptVO loadAssocInfo(SysDeptVO vo) {
        KaishiSysDeptVO kaishiSysDeptVO = new KaishiSysDeptVO(vo, deptAssocRepo.getTypeIdByDeptId(vo.getId()));
        return kaishiSysDeptVO;
    }

    @Override
    public void associateDelete(String loginAccount) {
        // 先删除关联信息再删主信息
        empAssocRepo.logicDelete(loginAccount);

    }
}
