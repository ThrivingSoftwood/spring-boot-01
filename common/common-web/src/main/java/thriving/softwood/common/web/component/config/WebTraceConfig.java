package thriving.softwood.common.web.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import io.micrometer.tracing.Tracer;
import thriving.softwood.common.web.component.filter.WebTraceFilter;

@AutoConfiguration
public class WebTraceConfig {

    @Bean
    public FilterRegistrationBean<WebTraceFilter> webTraceFilterRegistration(Tracer tracer) {
        FilterRegistrationBean<WebTraceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new WebTraceFilter(tracer));
        registration.addUrlPatterns("/*");
        registration.setName("webTraceResponseFilter");
        return registration;
    }
}