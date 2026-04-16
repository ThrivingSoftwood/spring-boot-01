package thriving.softwood.kaishi.biz.api.system;

import java.util.List;

import thriving.softwood.kaishi.biz.pojo.record.DepartmentReq;
import thriving.softwood.kaishi.biz.pojo.vo.DepartmentVO;
import thriving.softwood.kaishi.biz.pojo.vo.SysDeptVO;

public interface DepartmentApi {
    List<SysDeptVO> treeDepartments();

    List<DepartmentVO> treeUnsyncedDepartments();

    void syncDepartments(List<String> typeIds);

    void update(DepartmentReq departmentReq);

    String delete(DepartmentReq departmentReq);
}
