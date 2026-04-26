package thriving.softwood.common.web.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import io.micrometer.observation.ObservationRegistry;

/**
 * 🚀 Spring Boot 4.0.6 客户端观测性自动配置
 * <p>
 * 解决了 RestClient.Builder 与 ObservationRegistry 的时序装配问题。 在 Spring Boot 4.x 中，Observation 是跨越 Tracing 和 Metrics 的核心抽象。
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-01-29
 */
@AutoConfiguration
@ConditionalOnClass({RestClient.class, ObservationRegistry.class})
// ⚡ 关键：必须在 RestClient 官方配置加载后再注入，否则 Builder 无法在上下文中找到
@AutoConfigureAfter(name = {"org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration",
    "org.springframework.boot.restclient.autoconfigure.RestClientObservationAutoConfiguration"})
public class RestClientConfig {

    /**
     * 注入并配置 RestClient 在 Spring Boot 4 中，推荐通过 Builder 链式调用
     *
     * @param builder 由 RestClientAutoConfiguration 提供的原型对象
     * @param observationRegistry 观测注册表，用于自动拦截请求并生成 Span
     */
    @Bean
    @ConditionalOnBean(ObservationRegistry.class)
    @ConditionalOnMissingBean
    @Scope("prototype") // 建议使用原型模式，防止不同 Service 间的 Builder 污染
    public RestClient restClient(RestClient.Builder builder, ObservationRegistry observationRegistry) {
        return builder
            // 核心：将 Observation 机制挂载到 RestClient
            // 这会自动触发跨服务的 TraceContext 注入 (Header: traceparent)
            .observationRegistry(observationRegistry).build();
    }

    /**
     * 为旧式代码提供具备观测能力的 RestTemplate
     */
    @Bean
    @ConditionalOnBean(ObservationRegistry.class)
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(ObservationRegistry observationRegistry) {
        RestTemplate restTemplate = new RestTemplate();
        // Spring 4 会自动检测 restTemplate 的 observationRegistry 并注入拦截器
        restTemplate.setObservationRegistry(observationRegistry);
        return restTemplate;
    }
}