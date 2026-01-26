package thriving.softwood.common.web.component.filter;

import static thriving.softwood.common.core.constant.WebKeyConstant.Headers.X_TRACE_ID;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Web 响应增强过滤器
 * <p>
 * 注意：Spring Boot Micrometer 已经自动处理了 Trace 的生成和提取。 本过滤器的唯一职责是将当前的 traceId 写入 Response Header，方便客户端排查。
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-01-26
 */
@Order(Ordered.LOWEST_PRECEDENCE - 100) // 确保在 ObservationFilter 之后执行，这样 tracer 才有值
public class WebTraceFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    public WebTraceFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            // 获取标准的 32位 traceId
            String traceId = currentSpan.context().traceId();
            response.setHeader(X_TRACE_ID, traceId);
        }

        filterChain.doFilter(request, response);
    }
}