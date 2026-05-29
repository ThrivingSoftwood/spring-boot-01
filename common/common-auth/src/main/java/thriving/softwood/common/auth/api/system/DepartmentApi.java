package thriving.softwood.common.auth.api.system;

import java.util.List;

import thriving.softwood.common.auth.pojo.dto.DepartmentDTO;
import thriving.softwood.common.security.pojo.vo.SysDeptVO;

public interface DepartmentApi {
    List<SysDeptVO> treeDepartments();

    void update(DepartmentDTO kaishiDepartmentReq);

    String delete(DepartmentDTO kaishiDepartmentReq);
}
