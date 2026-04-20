package thriving.softwood.common.auth.api.login;

import thriving.softwood.common.auth.pojo.record.LoginReq;
import thriving.softwood.common.auth.pojo.record.LoginResp;
import thriving.softwood.common.auth.pojo.record.PasswordReq;

public interface AuthApi {

    LoginResp login(LoginReq req);

    void changePassword(PasswordReq req);

    LoginResp refreshPermission();
}