package thriving.softwood.common.web.component.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import cn.hutool.v7.core.convert.ConvertUtil;
import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.json.jwt.JWTPayload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import thriving.softwood.common.core.exception.TokenException;
import thriving.softwood.common.core.util.JwtUtil;
import thriving.softwood.common.framework.context.UserContext;

public class JwtInterceptor implements HandlerInterceptor {

    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (StrUtil.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new TokenException("未提供授权 Token 或格式错误");
        }

        String token = authHeader.substring(7);
        JWTPayload payload = JwtUtil.verifyAndParse(token);

        // 提取信息并存入 ThreadLocal
        Long userId = ConvertUtil.toLong(payload.getClaim("userId"));
        String loginAccount = payload.getClaim("loginAccount").toString();
        // todo: 改造后这里再获取 roleCode 进行实际的判断 Boolean godMode = SUPER_ADMIN_ROLE.equals(roleCode);
        Boolean godMode = true;

        UserContext.set(userId, loginAccount, godMode);
        // 放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex) {
        // ⚠️ 极其重要：防止 Tomcat 线程池复用导致的内存泄漏和越权漏洞
        UserContext.clear();
    }
}