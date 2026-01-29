package thriving.softwood.common.web.component.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Web Span 命名规范化拦截器
 * <p>
 * 作用：将 HTTP 入口 Span 的名称从默认的 "HTTP Method + URL" (如 GET /user/1) 修改为更具业务含义的 "Controller类名.方法名" (如
 * UserController.getUser)。
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-01-29
 */
@RequiredArgsConstructor
public class WebSpanNameInterceptor implements HandlerInterceptor {

    private final Tracer tracer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 获取当前由 Spring Boot 自动生成的 HTTP Span
        Span currentSpan = tracer.currentSpan();

        // 2. 仅当 Handler 是具体的 Controller 方法时才重命名
        // (如果是静态资源映射 ResourceHttpRequestHandler，则跳过)
        if (currentSpan != null && handler instanceof HandlerMethod handlerMethod) {
            // 3. 提取规范化名称：ClassName.MethodName
            String className = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();
            String newName = className + "." + methodName;

            // 4. 重命名当前 Span
            currentSpan.name(newName);

            // 可选：你甚至可以将原本的 URL 作为 Tag 补充进去，防止丢失路径信息
            currentSpan.tag("http.route.original", request.getRequestURI());
        }

        return true;
    }
}