package thriving.softwood.common.auth.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thriving.softwood.common.auth.api.system.DepartmentApi;
import thriving.softwood.common.auth.pojo.record.DepartmentReq;
import thriving.softwood.common.auth.pojo.vo.SysDeptVO;
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
    public Result update(@RequestBody DepartmentReq departmentReq) {
        departmentApi.update(departmentReq);
        return Result.success();
    }

    @RequestMapping("/delete")
    public Result<String> delete(@RequestBody DepartmentReq departmentReq) {
        return Result.success(departmentApi.delete(departmentReq));
    }

}
