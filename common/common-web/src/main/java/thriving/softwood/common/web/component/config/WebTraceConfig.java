package thriving.softwood.common.web.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import thriving.softwood.common.web.component.filter.TraceFilter;
import thriving.softwood.common.web.component.interceptor.TraceRestTemplateInterceptor;

/**
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
@AutoConfiguration
public class WebTraceConfig {

    @Bean
    public FilterRegistrationBean<TraceFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceFilter());
        // 拦截所有路径
        registration.addUrlPatterns("/*");
        registration.setName("traceFilter");
        // 再次确保优先级
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }

    // =========================================================
    // 🆕 新增：客户端透传配置
    // =========================================================

    /**
     * 注册拦截器 Bean
     */
    @Bean
    public TraceRestTemplateInterceptor traceRestTemplateInterceptor() {
        return new TraceRestTemplateInterceptor();
    }

    /**
     * 配置 RestTemplate 定制器
     * <p>
     * 只要用户使用 @Autowired RestTemplateBuilder builder; builder.build() 创建客户端， 就会自动挂载我们的拦截器。 TODO:
     * 未经测试的方法,只实现了逻辑.请在使用出现问题时调整.
     * </p>
     */
    @Bean
    @ConditionalOnClass(RestTemplate.class)
    @ConditionalOnMissingBean(RestTemplateCustomizer.class) // 允许用户覆盖
    public RestTemplateCustomizer traceRestTemplateCustomizer(TraceRestTemplateInterceptor interceptor) {
        return restTemplate -> restTemplate.getInterceptors().add(interceptor);
    }
}