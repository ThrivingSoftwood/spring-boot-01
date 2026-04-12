package thriving.softwood.common.web.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.Resource;
import thriving.softwood.common.web.component.interceptor.JwtInterceptor;
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
        // 实例化并注册我们的 JWT 拦截器
        registry.addInterceptor(new JwtInterceptor())
            // 1. 指定需要拦截的 URL 模式 (拦截所有 /api/ 下的请求)
            .addPathPatterns("/**")

            // 2. 指定需要放行的 URL 模式 (白名单)
            // 登录接口绝对不能拦截，否则陷入死循环
            .excludePathPatterns("/**/auth/login",
                // 预留的注册接口(如有)
                "/api/auth/register",
                // Spring Boot 默认的错误处理路径
                "/error",
                // 网站图标
                "/favicon.ico",
                // 如果后续接入 OpenAPI/Swagger 文档
                "/swagger-ui/**", "/v3/api-docs/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有路径应用 CORS 配置
            // 允许来自你 Vue 前端的源 (!!! 非常重要 !!!)
            // 1. 开发环境: 通常是 http://localhost:xxxx 或 http://127.0.0.1:xxxx
            // 2. 部署环境: 可能是你的 Windows 11 机器的 IP 地址或域名
            // 例如: "http://192.168.1.100:5173" (假设 Vue 运行在 5173 端口)
            // 或者使用 allowedOriginPatterns 支持更灵活的模式
            .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173", "http://192.168.1.4:5173",
                "http://192.168.1.8:5173", "http://192.168.1.6:5173"
            // 如果部署后有固定域名/IP，也加上
            )
            // 或者使用 allowedOriginPatterns (更灵活, 支持通配符)
            .allowedOriginPatterns("http://localhost:*", "http://192.168.1.*:5173")

            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD") // 允许的 HTTP 方法
            // 允许所有请求头
            .allowedHeaders("*")
            // 是否允许发送 Cookie
            .allowCredentials(true)
            // 预检请求 (OPTIONS) 的缓存时间 (秒)
            .maxAge(3600);
    }
}