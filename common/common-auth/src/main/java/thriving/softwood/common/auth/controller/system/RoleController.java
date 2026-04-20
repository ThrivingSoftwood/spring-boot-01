// RoleController.java
package thriving.softwood.common.auth.controller.system;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import thriving.softwood.common.auth.api.system.RoleApi;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysRole;
import thriving.softwood.common.auth.pojo.record.RoleReq;
import thriving.softwood.common.core.result.Result;

@RestController
@RequestMapping("/system/role")
public class RoleController {
    private final RoleApi roleApi;

    public RoleController(RoleApi roleApi) {
        this.roleApi = roleApi;
    }

    @GetMapping("/list")
    public Result<List<SysRole>> listRoles() {
        return Result.success(roleApi.listRoles());
    }

    @PostMapping("/save")
    public Result<String> saveOrUpdate(@RequestBody RoleReq req) {
        roleApi.saveOrUpdateRole(req);
        return Result.success("保存成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteRole(@PathVariable Long id) {
        roleApi.deleteRole(id);
        return Result.success("删除成功");
    }

    // --- 分配信息的查询 ---
    @GetMapping("/assigned-users/{id}")
    public Result<List<Long>> getAssignedUsers(@PathVariable Long id) {
        return Result.success(roleApi.getAssignedUserIds(id));
    }

    @GetMapping("/assigned-permissions/{id}")
    public Result<List<Long>> getAssignedPermissions(@PathVariable Long id) {
        return Result.success(roleApi.getAssignedPermissionIds(id));
    }

    @GetMapping("/assigned-data-rules/{id}")
    public Result<List<Long>> getAssignedDataRules(@PathVariable Long id) {
        return Result.success(roleApi.getAssignedDataRuleIds(id));
    }

    // --- 执行分配操作 ---
    @PostMapping("/assign-users")
    public Result<String> assignUsers(@RequestBody RoleReq req) {
        roleApi.assignUsers(req);
        return Result.success("用户分配成功");
    }

    @PostMapping("/assign-permissions")
    public Result<String> assignPermissions(@RequestBody RoleReq req) {
        roleApi.assignPermissions(req);
        return Result.success("权限分配成功");
    }

    @PostMapping("/assign-data-rules")
    public Result<String> assignDataRules(@RequestBody RoleReq req) {
        roleApi.assignDataRules(req);
        return Result.success("数据规则分配成功");
    }
}