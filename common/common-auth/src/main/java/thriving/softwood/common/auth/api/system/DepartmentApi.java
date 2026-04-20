package thriving.softwood.common.auth.api.system;

import java.util.List;

import thriving.softwood.common.auth.pojo.record.DepartmentReq;
import thriving.softwood.common.auth.pojo.vo.SysDeptVO;

public interface DepartmentApi {
    List<SysDeptVO> treeDepartments();

    void update(DepartmentReq departmentReq);

    String delete(DepartmentReq departmentReq);
}
