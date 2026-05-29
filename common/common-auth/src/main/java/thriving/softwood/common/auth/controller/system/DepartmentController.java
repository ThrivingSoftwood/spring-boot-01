package thriving.softwood.common.auth.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thriving.softwood.common.auth.api.system.DepartmentApi;
import thriving.softwood.common.auth.pojo.dto.DepartmentDTO;
import thriving.softwood.common.security.pojo.vo.SysDeptVO;
import thriving.softwood.common.core.result.Result;

@RestController
@RequestMapping("/system/dept")
public class DepartmentController {
    DepartmentApi departmentApi;

    @Autowired
    public DepartmentController(DepartmentApi departmentApi) {
        this.departmentApi = departmentApi;
    }

    @RequestMapping("/tree")
    public Result<List<SysDeptVO>> getTree() {
        return Result.success(departmentApi.treeDepartments());
    }

    @RequestMapping("/update")
    public Result update(@RequestBody DepartmentDTO dto) {
        departmentApi.update(dto);
        return Result.success();
    }

    @RequestMapping("/delete")
    public Result<String> delete(@RequestBody DepartmentDTO dto) {
        return Result.success(departmentApi.delete(dto));
    }

}
