package thriving.softwood.common.web.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import thriving.softwood.common.web.component.filter.TraceFilter;

@AutoConfiguration
public class WebTraceConfig {

    @Bean
    public FilterRegistrationBean<TraceFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceFilter());
        registration.addUrlPatterns("/*"); // 拦截所有路径
        registration.setName("traceFilter");
        registration.setOrder(Integer.MIN_VALUE); // 再次确保优先级
        return registration;
    }
}