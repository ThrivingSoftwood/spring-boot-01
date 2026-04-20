package thriving.softwood.kaishi.biz.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.kaishi.biz.api.system.UserApi;
import thriving.softwood.kaishi.biz.pojo.record.UserReq;
import thriving.softwood.kaishi.biz.pojo.vo.OrgNodeVO;

@RestController
@RequestMapping("/kaishi/system/user")
public class UserController {

    private final UserApi userApi;

    @Autowired
    public UserController(UserApi userApi) {
        this.userApi = userApi;
    }

    @PostMapping("/tree/synced")
    public Result<List<OrgNodeVO>> treeSynced() {
        return Result.success(userApi.treeSynced());
    }

    @PostMapping("/tree/unsynced")
    public Result<List<OrgNodeVO>> treeUnsynced() {
        return Result.success(userApi.treeUnsynced());
    }

    @PostMapping("/sync")
    public Result<String> sync(@RequestBody UserReq req) {
        userApi.sync(req);
        return Result.success("人员引入成功");
    }

    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody UserReq req) {
        userApi.updateUser(req);
        return Result.success("更新成功");
    }

    @PutMapping("/reset/pwd")
    public Result<String> resetPassword(@RequestBody UserReq req) {
        userApi.resetPassword(req);
        return Result.success("密码重置成功");
    }

    /**
     * 🌟 获取指定用户已拥有的角色 ID 列表
     */
    @GetMapping("/assigned-roles/{userId}")
    public Result<List<Long>> listAssignedRoles(@PathVariable Long userId) {
        return Result.success(userApi.listAssignedRoleIdsByUserId(userId));
    }

    /**
     * 🌟 为用户分配角色 接收参数：{ "id": 1, "roleIds": [1, 2, 5] }
     */
    @PostMapping("/assign-roles")
    public Result<String> assignRoles(@RequestBody UserReq req) {
        userApi.assignRoles(req.id(), req.roleIds());
        return Result.success("角色分配成功，权限已同步");
    }
}