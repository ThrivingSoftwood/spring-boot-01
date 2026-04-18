package thriving.softwood.kaishi.biz.controller;

import static thriving.softwood.common.core.enums.RespCodeEnum.INTERNAL_SERVER_ERROR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.kaishi.biz.api.login.AuthApi;
import thriving.softwood.kaishi.biz.pojo.record.LoginReq;
import thriving.softwood.kaishi.biz.pojo.record.LoginResp;
import thriving.softwood.kaishi.biz.pojo.record.PasswordReq;

/**
 * @author ThrivingSoftwood
 */
@RestController
@RequestMapping("/kaishi/auth")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthApi authApi;

    @Autowired
    public AuthController(AuthApi authApi) {
        this.authApi = authApi;
    }

    @RequestMapping("/login")
    public Result<LoginResp> doLogin(@RequestBody LoginReq data) {
        try {
            return Result.success(authApi.login(data));
        } catch (Exception e) {
            logger.error("登录过程中发生异常!", e);
            return Result.error(INTERNAL_SERVER_ERROR.code(), e.getLocalizedMessage());
        }
    }

    @RequestMapping("/changePassword")
    public Result changePassword(@RequestBody PasswordReq req) {
        try {
            authApi.changePassword(req);
            return Result.success();
        } catch (Exception e) {
            return Result.error(INTERNAL_SERVER_ERROR.code(), e.getLocalizedMessage());
        }
    }

    @RequestMapping("/refreshPerm")
    public Result<LoginResp> refreshPerm() {
        return Result.success(authApi.refreshPermission());
    }
}
