package thriving.softwood.common.security.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import cn.hutool.v7.core.convert.ConvertUtil;
import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.json.jwt.JWTPayload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import thriving.softwood.common.core.exception.TokenException;
import thriving.softwood.common.core.util.JwtUtil;
import thriving.softwood.common.security.api.provider.UserAuthProviderApi;
import thriving.softwood.common.security.context.UserContext;
import thriving.softwood.common.security.pojo.dto.UserAuthInfoDTO;

public class JwtInterceptor implements HandlerInterceptor {

    private final UserAuthProviderApi userAuthProviderApi;

    // 通过 WebMvcConfig 注册时，将 AuthCacheSvc 注入进来
    public JwtInterceptor(UserAuthProviderApi userAuthProviderApi) {
        this.userAuthProviderApi = userAuthProviderApi;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (StrUtil.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new TokenException("未提供授权 Token 或格式错误");
        }

        String token = authHeader.substring(7);
        JWTPayload payload = JwtUtil.verifyAndParse(token);

        Long userId = ConvertUtil.toLong(payload.getClaim("userId"));
        String loginAccount = payload.getClaim("loginAccount").toString();

        // 🌟 1. 从 JWT 取出前端持有的版本号
        String jwtPermVersion =
            payload.getClaim("permVersion") != null ? payload.getClaim("permVersion").toString() : "";

        // 🌟 2. 从 Caffeine 缓存获取用户当前的实时权限信息 (命中缓存时耗时不到 1 微秒)
        UserAuthInfoDTO authInfo = userAuthProviderApi.getAuthInfo(userId);

        if (authInfo == null || authInfo.getStatus() == 0) {
            throw new TokenException("账户不存在或已被禁用，请联系管理员");
        }

        // 检查所属部门状态
        if (!authInfo.getDepartmentActive() && !authInfo.getGodMode()) {
            // 注意：上帝模式（SUPER_ADMIN 角色账号）通常跳过部门检查，防止系统死锁
            throw new TokenException("所属部门已被禁用，请联系管理员");
        }

        // 🌟 3. 无感刷新防线：版本比对
        // 如果后端管理员修改了权限，导致 authInfo 的版本号变了，通知前端静默刷新
        if (StrUtil.isNotBlank(authInfo.getPermissionVersion())
            && !authInfo.getPermissionVersion().equals(jwtPermVersion)) {
            response.setHeader("X-Update-Perm", "true");
            response.setHeader("Access-Control-Expose-Headers", "X-Update-Perm");
        }

        // 🌟 5. 注入全量上下文，供 MyBatis-Plus 和 Jackson 拦截器使用
        UserContext.set(authInfo);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex) {
        UserContext.clear();
    }
}