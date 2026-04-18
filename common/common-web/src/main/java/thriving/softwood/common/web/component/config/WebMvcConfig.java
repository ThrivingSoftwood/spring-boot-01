package thriving.softwood.common.web.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
        // 拦截所有路径
        registry.addInterceptor(new WebSpanNameInterceptor(tracer)).addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 对所有路径应用 CORS 配置
        registry.addMapping("/**")
            // 允许来自你 Vue 前端的源 (!!! 非常重要 !!!)
            // 1. 开发环境: 通常是 http://localhost:xxxx 或 http://127.0.0.1:xxxx
            // 2. 部署环境: 可能是你的 Windows 11 机器的 IP 地址或域名
            // 例如: "http://192.168.1.100:5173" (假设 Vue 运行在 5173 端口)
            // 或者使用 allowedOriginPatterns (更灵活, 支持通配符)
            .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*", "http://192.*:*", "http://10.*:*",
                "http://172.*:*", "http://*:*")
            // 允许的 HTTP 方法
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
            // 允许所有请求头
            .allowedHeaders("*")
            // 是否允许发送 Cookie
            .allowCredentials(true)
            // 预检请求 (OPTIONS) 的缓存时间 (秒)
            .maxAge(3600);
    }
}