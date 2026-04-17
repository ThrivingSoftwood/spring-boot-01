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

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userApi.deleteUser(id);
        return Result.success("用户移除成功");
    }
}