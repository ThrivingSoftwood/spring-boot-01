package thriving.softwood.common.web.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.Resource;
import thriving.softwood.common.web.component.interceptor.WebSpanNameInterceptor;

/**
 * Spring MVC 扩展配置
 *
 * @author ThrivingSoftwood
 * @since 2026-01-29
 */
@AutoConfiguration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private Tracer tracer;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Span 重命名拦截器
        // 建议顺序：靠前执行，确保后续的过滤器或切面能拿到已规范化的名称
        registry.addInterceptor(new WebSpanNameInterceptor(tracer)).addPathPatterns("/**"); // 拦截所有路径
    }
}