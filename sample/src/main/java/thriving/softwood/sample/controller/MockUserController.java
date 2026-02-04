package thriving.softwood.sample.controller;

import static thriving.softwood.common.core.enums.RespCodeEnum.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import thriving.softwood.common.core.result.Result;
import thriving.softwood.sample.api.MockUserApi;
import thriving.softwood.sample.pojo.entity.User;

@RestController
@RequestMapping(path = "/mock/user/")
public class MockUserController {

    @Resource
    MockUserApi mockUserApi;

    @GetMapping(path = "/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        return Result.success(mockUserApi.getUserById(id));
    }

    /**
     * 使用 @RequestBody 将入参 user 对象转换为 JSON 数据
     * 
     * @param user
     * @return
     */
    @PostMapping(path = "/add")
    public Result addUser(@RequestBody User user) {
        try {
            mockUserApi.addOrModifyUser(user);
        } catch (Exception e) {
            return Result.error(INTERNAL_SERVER_ERROR, e);
        }
        return Result.success("新增成功");
    }

    @PutMapping(path = "/{id}")
    public Result modifyUser(@RequestBody User user) {
        try {
            mockUserApi.addOrModifyUser(user);
        } catch (Exception e) {
            return Result.error(INTERNAL_SERVER_ERROR, e);
        }
        return Result.success("更新成功");
    }

    @DeleteMapping(path = "/id")
    public Result removeUserById(@PathVariable Integer id) {
        try {
            mockUserApi.removeUserById(id);
        } catch (Exception e) {
            return Result.error(INTERNAL_SERVER_ERROR, e);
        }
        return Result.success("删除成功");
    }

}
