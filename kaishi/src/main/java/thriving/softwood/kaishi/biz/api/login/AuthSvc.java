package thriving.softwood.kaishi.biz.api.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.v7.core.date.DateUtil;
import cn.hutool.v7.crypto.digest.BCrypt;
import thriving.softwood.common.core.util.JwtUtil;
import thriving.softwood.common.core.util.Sm4Util;
import thriving.softwood.kaishi.biz.pojo.record.LoginReq;
import thriving.softwood.kaishi.biz.pojo.record.LoginResp;
import thriving.softwood.kaishi.biz.pojo.record.PasswordReq;
import thriving.softwood.kaishi.infrastructure.db.master.entity.SysUser;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysUserRepo;

@Service
public class AuthSvc implements AuthApi {

    private final SysUserRepo sysUserRepo;

    public AuthSvc(SysUserRepo sysUserRepo, thriving.softwood.common.core.util.JwtUtil jwtUtil) {
        this.sysUserRepo = sysUserRepo;
    }

    @Override
    @Transactional
    public LoginResp login(LoginReq req) {
        // 1. 约束检查：只允许固定账户
        if (!"kaishi".equals(req.loginAccount())) {
            throw new RuntimeException("非法访问：系统仅允许授权账户登录");
        }

        // 2. SM4 解密前端传来的密文 (CBC/PKCS5Padding)
        String plainPassword;
        try {
            plainPassword = Sm4Util.decWeb(req.encryptedPassword());
        } catch (Exception e) {
            throw new RuntimeException("非法请求：密码解密失败");
        }

        // 3. 查询数据库获取用户信息
        SysUser user = sysUserRepo.getByLoginAccount(req.loginAccount());
        if (user == null || user.getStatus() == 0) {
            throw new RuntimeException("账户不存在或已被禁用");
        }

        // 4. BCrypt 校验哈希密文
        if (!BCrypt.checkpw(plainPassword, user.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }

        // 5. 更新最后登录时间 (注意 DB 是 varchar(30))
        // 格式：yyyy-MM-dd HH:mm:ss
        String currentTimeStr = DateUtil.formatNow();
        user.setLoginTime(currentTimeStr);
        // MyBatis-Plus 操作
        sysUserRepo.updateById(user);

        // 6. 签发 RS256 JWT
        String token = JwtUtil.generateToken(user.getId(), user.getLoginAccount());

        return new LoginResp(token, user.getUsername());
    }

    @Override
    public void changePassword(PasswordReq req) {

        // 2. SM4 解密前端传来的密文 (CBC/PKCS5Padding)
        String plainPassword;
        try {
            plainPassword = Sm4Util.decWeb(req.oldPasswordEnc());
        } catch (Exception e) {
            throw new RuntimeException("非法请求：原密码解密失败");
        }

        // 3. 查询数据库获取用户信息
        SysUser user = sysUserRepo.getByLoginAccount(req.loginAccount());

        // 4. BCrypt 校验哈希密文
        if (!BCrypt.checkpw(plainPassword, user.getPassword())) {
            throw new RuntimeException("原密码输入错误");
        }

        user.setLastPassword(plainPassword);

        try {
            plainPassword = Sm4Util.decWeb(req.newPasswordEnc());
        } catch (Exception e) {
            throw new RuntimeException("非法请求：新密码解密失败");
        }
        if (plainPassword.equals(user.getLastPassword())) {
            throw new RuntimeException("新密码不能与老密码相同!");
        }
        user.setPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
        // MyBatis-Plus 操作
        sysUserRepo.updateById(user);
    }
}