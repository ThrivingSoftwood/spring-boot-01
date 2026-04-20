// File: ./kaishi/src/main/java/thriving/softwood/kaishi/biz/controller/sync/ErpSyncController.java
package thriving.softwood.kaishi.biz.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import thriving.softwood.common.core.result.Result;
import thriving.softwood.kaishi.biz.api.sync.ErpSyncSvc;
import thriving.softwood.kaishi.biz.pojo.record.ErpSyncReq;
import thriving.softwood.kaishi.biz.pojo.vo.DepartmentVO;
import thriving.softwood.kaishi.biz.pojo.vo.OrgNodeVO;

/**
 * 管家婆 ERP 数据同步控制器
 */
@RestController
@RequestMapping("/kaishi/sync")
public class ErpSyncController {

    private final ErpSyncSvc erpSyncSvc;

    public ErpSyncController(ErpSyncSvc erpSyncSvc) {
        this.erpSyncSvc = erpSyncSvc;
    }

    @PostMapping("/user/tree-unsynced")
    public Result<List<OrgNodeVO>> treeUnsyncedUsers() {
        return Result.success(erpSyncSvc.treeUnsyncedUsers());
    }

    @PostMapping("/user")
    public Result<String> syncUsers(@RequestBody ErpSyncReq req) {
        erpSyncSvc.syncUsers(req);
        return Result.success("人员引入成功");
    }

    @GetMapping("/dept/tree-unsynced")
    public Result<List<DepartmentVO>> treeUnsyncedDepartments() {
        return Result.success(erpSyncSvc.treeUnsyncedDepartments());
    }

    @PostMapping("/dept")
    public Result<String> syncDepartments(@RequestBody ErpSyncReq req) {
        erpSyncSvc.syncDepartments(req.deptTypeIds());
        return Result.success("部门引入成功");
    }
}