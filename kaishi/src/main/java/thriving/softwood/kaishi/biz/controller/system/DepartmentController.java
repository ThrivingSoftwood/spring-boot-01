package thriving.softwood.kaishi.biz.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.kaishi.biz.api.system.DepartmentApi;
import thriving.softwood.kaishi.biz.pojo.record.DepartmentReq;
import thriving.softwood.kaishi.biz.pojo.vo.DepartmentVO;
import thriving.softwood.kaishi.biz.pojo.vo.SysDeptVO;

@RestController
@RequestMapping("/kaishi/system/dept")
public class DepartmentController {
    DepartmentApi departmentApi;

    @Autowired
    public DepartmentController(DepartmentApi departmentApi) {
        this.departmentApi = departmentApi;
    }

    @RequestMapping("/tree/synced")
    public Result<List<SysDeptVO>> getTree() {
        return Result.success(departmentApi.treeDepartments());
    }

    @RequestMapping("/tree/unsynced")
    public Result<List<DepartmentVO>> treeUnsyncedDepartments() {
        return Result.success(departmentApi.treeUnsyncedDepartments());
    }

    @RequestMapping("/sync")
    public Result sync(@RequestBody DepartmentReq departmentReq) {
        departmentApi.syncDepartments(departmentReq.typeIds());
        return Result.success();
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
