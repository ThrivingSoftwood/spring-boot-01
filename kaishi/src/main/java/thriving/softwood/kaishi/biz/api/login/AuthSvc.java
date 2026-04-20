package thriving.softwood.kaishi.biz.api.login;

import org.springframework.stereotype.Service;

import cn.hutool.v7.core.date.DateUtil;
import cn.hutool.v7.crypto.digest.BCrypt;
import thriving.softwood.common.core.util.JwtUtil;
import thriving.softwood.common.core.util.Sm4Util;
import thriving.softwood.kaishi.biz.api.system.AuthCacheSvc;
import thriving.softwood.kaishi.biz.pojo.dto.UserAuthInfoDTO;
import thriving.softwood.kaishi.biz.pojo.record.LoginReq;
import thriving.softwood.kaishi.biz.pojo.record.LoginResp;
import thriving.softwood.kaishi.biz.pojo.record.PasswordReq;
import thriving.softwood.kaishi.context.UserContext;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysUserRepo;

@Service
public class AuthSvc implements AuthApi {

    private final SysUserRepo sysUserRepo;
    // 🌟 注入权限查询服务
    private final AuthCacheSvc authCacheSvc;

    public AuthSvc(SysUserRepo sysUserRepo, AuthCacheSvc authCacheSvc) {
        this.sysUserRepo = sysUserRepo;
        this.authCacheSvc = authCacheSvc;
    }

    @Override
    public LoginResp login(LoginReq req) {

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

        // 🌟 5. 核心修复：引入部门活跃度检查（拒敌于国门之外）
        // 我们直接调用之前写好的权限大脑，它内部已经包含了“上帝模式判定”和“部门状态判定”
        UserAuthInfoDTO authInfo = authCacheSvc.getUserAuthInfo(user.getId());

        if (!authInfo.getGodMode() && !authInfo.getDepartmentActive()) {
            throw new RuntimeException("登录失败：您所属的部门目前处于禁用状态");
        }

        // 4. BCrypt 校验哈希密文
        if (!BCrypt.checkpw(plainPassword, user.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }

        // 5. 更新最后登录时间 (注意 DB 是 varchar(30))
        // 格式：yyyy-MM-dd HH:mm:ss
        String currentTimeStr = DateUtil.formatNow();
        user.setLoginTime(currentTimeStr);
        // 如果用户的版本号为空（新开通的账号），初始化一个版本号
        if (user.getPermissionVersion() == null) {
            user.setPermissionVersion(DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss"));
        }
        sysUserRepo.updateById(user);

        // 🌟 1. 签发 JWT，必须带上版本号
        String token = JwtUtil.generateToken(user.getId(), user.getLoginAccount(), user.getPermissionVersion());

        // 🌟 2. 查出用户的权限标识列表 (如 ["purchase:trace:list", "purchase:price:view"])

        // 🌟 3. 构造增强版的 LoginResp
        return new LoginResp(token, user.getUsername(), authInfo.getPermissions(), authInfo.getRoleCodes());
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
        // 🌟 密码修改属于高危操作，强制刷新权限版本号，使前端静默刷新或拦截旧 Token
        user.setPermissionVersion(DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss"));
        sysUserRepo.updateById(user);
    }

    @Override
    public LoginResp refreshPermission() {
        // 此时 JwtInterceptor 已经放行，UserContext 里有当前用户 ID
        Long userId = UserContext.userId();
        SysUser user = sysUserRepo.getById(userId);

        UserAuthInfoDTO authInfo = authCacheSvc.getUserAuthInfo(userId);

        // 2. 签发全新 Token
        String newToken = JwtUtil.generateToken(userId, user.getLoginAccount(), user.getPermissionVersion());

        return new LoginResp(newToken, user.getUsername(), authInfo.getPermissions(), authInfo.getRoleCodes());
    }
}