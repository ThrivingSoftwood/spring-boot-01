package thriving.softwood.common.auth.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import thriving.softwood.common.auth.api.system.UserApi;
import thriving.softwood.common.auth.pojo.dto.UserDTO;
import thriving.softwood.common.auth.pojo.vo.OrganizationNodeVO;
import thriving.softwood.common.core.result.Result;

@RestController
@RequestMapping("/system/user")
public class UserController {

    private final UserApi userApi;

    @Autowired
    public UserController(UserApi userApi) {
        this.userApi = userApi;
    }

    @PostMapping("/tree")
    public Result<List<OrganizationNodeVO>> treeSynced() {
        return Result.success(userApi.treeSynced());
    }

    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody UserDTO dto) {
        userApi.updateUser(dto);
        return Result.success("更新成功");
    }

    @PutMapping("/reset/pwd")
    public Result<String> resetPassword(@RequestBody UserDTO dto) {
        userApi.resetPassword(dto);
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
    public Result<String> assignRoles(@RequestBody UserDTO dto) {
        userApi.assignRoles(dto.getId(), dto.getRoleIds());
        return Result.success("角色分配成功，权限已同步");
    }

    /**
     * 🌟 删除用户
     */
    @DeleteMapping("/delete/{userId}")
    public Result<String> delete(@PathVariable Long userId) {
        userApi.deleteUser(userId);
        return Result.success("角色删除成功!");
    }
}