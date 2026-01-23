package thriving.softwood.common.web.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import thriving.softwood.common.web.component.filter.TraceFilter;

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
}