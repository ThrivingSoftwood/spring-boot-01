package thriving.softwood.kaishi.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;
import thriving.softwood.kaishi.biz.api.system.AuthCacheSvc;
import thriving.softwood.kaishi.component.interceptor.JwtInterceptor;

/**
 * Spring MVC 扩展配置
 *
 * @author ThrivingSoftwood
 * @since 2026-01-29
 */
@AutoConfiguration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AuthCacheSvc authCacheSvc;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 实例化并注册我们的 JWT 拦截器
        registry.addInterceptor(new JwtInterceptor(authCacheSvc))
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

}