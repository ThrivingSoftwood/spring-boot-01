package thriving.softwood.common.auth.controller.system;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import thriving.softwood.common.auth.api.system.PermissionApi;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysPermission;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysPermissionRepo;
import thriving.softwood.common.auth.pojo.vo.SysPermissionVO;
import thriving.softwood.common.core.result.Result;

@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    private final PermissionApi permissionApi;
    private final SysPermissionRepo permissionRepo;

    public PermissionController(PermissionApi permissionApi, SysPermissionRepo permissionRepo) {
        this.permissionApi = permissionApi;
        this.permissionRepo = permissionRepo;
    }

    @GetMapping("/tree")
    public Result<List<SysPermissionVO>> getTree() {
        return Result.success(permissionApi.treeAllPermissions());
    }

    @RequestMapping("/getMenuPermissions/{loginAccount}")
    public Result<List<SysPermissionVO>> treeMenuPermissions(@PathVariable String loginAccount) {
        return Result.success(permissionApi.treeMenuPermissions(loginAccount));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody SysPermission entity) {
        permissionApi.add(entity);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody SysPermission entity) {
        permissionApi.update(entity);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionApi.deletePermission(id);
        return Result.success();
    }
}