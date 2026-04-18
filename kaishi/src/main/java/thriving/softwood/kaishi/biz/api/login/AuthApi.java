package thriving.softwood.kaishi.biz.api.login;

import thriving.softwood.kaishi.biz.pojo.record.LoginReq;
import thriving.softwood.kaishi.biz.pojo.record.LoginResp;
import thriving.softwood.kaishi.biz.pojo.record.PasswordReq;

public interface AuthApi {

    LoginResp login(LoginReq req);

    void changePassword(PasswordReq req);

    LoginResp refreshPermission();
}