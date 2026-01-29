package thriving.softwood.common.web.component.interceptor;

import static thriving.softwood.common.core.constant.WebKeyConstant.Headers.X_TRACE_ID;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.common.logging.util.TraceUtil;

/**
 * RestTemplate/RestClient 链路追踪拦截器
 * <p>
 * 作用：在发起外部 HTTP 请求时，自动将当前线程的 TraceId 注入到 Request Header 中。 从而实现微服务之间的链路 ID 透传。
 * </p>
 *
 * @author ThrivingSoftwood
 * @since version 2026-01-29
 */
public class TraceRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TraceRestTemplateInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        // 1. 获取当前线程的 TraceId
        String traceId = TraceUtil.getTraceId();

        // 2. 如果存在，则注入到 Header 中
        if (StrUtil.isNotBlank(traceId)) {
            request.getHeaders().add(X_TRACE_ID, traceId);
            // 调试日志 (可选，生产环境建议调整级别)
            logger.trace("🌍 Outbound HTTP Request: Injecting {} = {}", X_TRACE_ID, traceId);
        } else {
            // 理论上进入 Filter 后一定有 TraceId，这里是防御性编程
            logger.warn("⚠️ Outbound HTTP Request: TraceId is missing in MDC context!");
        }

        // 3. 继续执行请求
        return execution.execute(request, body);
    }
}