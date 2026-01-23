package thriving.softwood.common.web.component.filter;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import thriving.softwood.common.logging.util.TraceUtil;

/**
 * Web 端链路追踪过滤器 优先级：最高 (Integer.MIN_VALUE)，保证是第一个执行
 *
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {
            // 1. 尝试从 Header 获取 TraceID (微服务透传)
            String traceId = request.getHeader(TraceUtil.TRACE_ID_HEADER);

            // 2. 开启追踪 (如果 Header 没有，Util 内部会自动生成)
            String currentTraceId = TraceUtil.start(traceId);

            // 3. 【关键】将 TraceID 写入 Response Header，方便前端/移动端排查问题
            response.setHeader(TraceUtil.TRACE_ID_HEADER, currentTraceId);

            // 4. 放行请求
            filterChain.doFilter(request, response);

        } finally {
            // 5. 请求结束，必须清理 MDC，防止线程污染
            TraceUtil.end();
        }
    }
}