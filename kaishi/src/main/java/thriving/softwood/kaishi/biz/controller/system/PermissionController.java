package thriving.softwood.kaishi.biz.controller.system;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.kaishi.biz.api.system.PermissionApi;
import thriving.softwood.kaishi.biz.pojo.vo.SysPermissionVO;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysPermission;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysPermissionRepo;

@RestController
@RequestMapping("/kaishi/system/permission")
public class PermissionController {

    private final PermissionApi sysPermissionApi;
    private final SysPermissionRepo permissionRepo;

    public PermissionController(PermissionApi sysPermissionApi, SysPermissionRepo permissionRepo) {
        this.sysPermissionApi = sysPermissionApi;
        this.permissionRepo = permissionRepo;
    }

    @GetMapping("/tree")
    public Result<List<SysPermissionVO>> getTree() {
        return Result.success(sysPermissionApi.treeAllPermissions());
    }

    @RequestMapping("/getMenuPermissions/{loginAccount}")
    public Result<List<SysPermissionVO>> treeMenuPermissions(@PathVariable String loginAccount) {
        return Result.success(sysPermissionApi.treeMenuPermissions(loginAccount));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody SysPermission entity) {
        permissionRepo.save(entity);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody SysPermission entity) {
        permissionRepo.updateById(entity);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysPermissionApi.deletePermission(id);
        return Result.success();
    }
}