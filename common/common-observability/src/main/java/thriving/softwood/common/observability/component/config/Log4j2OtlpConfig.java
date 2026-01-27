package thriving.softwood.common.observability.component.config;

import org.springframework.context.annotation.Configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.log4j.appender.v2_17.OpenTelemetryAppender;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * 🚀 Log4j2 OTLP 桥接配置 修复版：适配 opentelemetry-log4j-appender 2.24.0-alpha
 */
@Configuration
@RequiredArgsConstructor
public class Log4j2OtlpConfig {

    private final OpenTelemetry openTelemetry;

    @PostConstruct
    public void start() {
        // 核心：将 Spring 管理的 OTel 实例注入到 Log4j2 Appender
        // 这一步解决了 Log4j2 初始化早于 Spring Bean 的时序问题
        OpenTelemetryAppender.install(openTelemetry);
    }
}