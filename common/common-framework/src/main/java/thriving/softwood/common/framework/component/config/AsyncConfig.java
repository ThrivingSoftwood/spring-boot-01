package thriving.softwood.common.framework.component.config;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.NoArgsConstructor;
import thriving.softwood.common.framework.component.decorator.MicrometerTracingDecorator;

/**
 * 异步线程池配置 (Micrometer Native)
 *
 * @author ThrivingSoftwood
 * @since 2026-01-26
 */
@Configuration
@EnableAsync(order = Ordered.LOWEST_PRECEDENCE - 1)
public class AsyncConfig {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean("vtAsyncConfig")
    @ConfigurationProperties(prefix = "async.vt")
    public VtAsyncConfig vtAsyncConfig() {
        return new VtAsyncConfig();
    }

    @Bean("ptAsyncConfig")
    @ConfigurationProperties(prefix = "async.pt")
    public PtAsyncConfig ptAsyncConfig() {
        return new PtAsyncConfig();
    }

    /**
     * 🧱 平台线程池：适用于 CPU 密集型任务
     */
    @Bean("ptExecutor")
    public Executor ptExecutor(Tracer tracer, PtAsyncConfig ptAsyncConfig) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 依据机器核数设定：CPU核数 + 1
        int cores = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(ptAsyncConfig.getCorePoolSize());
        executor.setMaxPoolSize(ptAsyncConfig.getMaxPoolSize());
        // 队列不宜过大，防止积压导致内存溢出
        executor.setQueueCapacity(ptAsyncConfig.getQueueCapacity());
        executor.setThreadNamePrefix(ptAsyncConfig.getThreadNamePrefix());

        // 核心：挂载 Micrometer 装饰器
        executor.setTaskDecorator(new MicrometerTracingDecorator(tracer));

        executor.setRejectedExecutionHandler(ptAsyncConfig.getRejectedExecHandler());
        executor.initialize();
        return executor;
    }

    /**
     * 🚀 虚拟线程执行器：IO 密集型 (信号量模式)
     *
     * 变更点： 1. 切换回 SimpleAsyncTaskExecutor。 2. 启用 setVirtualThreads(true) 开启虚拟线程。 3. 启用 setConcurrencyLimit(3000)
     * 实现信号量限流。
     *
     * 性能优势： 移除了 ThreadPoolExecutor 中的 BlockingQueue 和 Worker 锁竞争。 当任务数达到 3000 时，新任务提交会直接阻塞 (Throttle)，直到有配额释放。 这是目前
     * Java 虚拟线程处理高并发 IO 的最佳实践。
     */
    @Bean("vtExecutor")
    public Executor vtExecutor(Tracer tracer, VtAsyncConfig vtAsyncConfig) {
        // 使用 SimpleAsyncTaskExecutor，它不进行池化，而是为每个任务创建一个新线程（这里是虚拟线程）
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor(vtAsyncConfig.getThreadNamePrefix());

        // 1. 开启虚拟线程
        executor.setVirtualThreads(true);

        // 2. 信号量限流 (Concurrency Throttle)
        // 限制同时处于活动状态的虚拟线程数为 3000 (根据实际内存调整)
        // 如果没有这个限制，恶意请求可能瞬间创建百万个虚拟线程导致 OOM
        executor.setConcurrencyLimit(vtAsyncConfig.getConcurrencyLimit());

        // 3. 挂载链路追踪装饰器 (依赖第一步)
        executor.setTaskDecorator(new MicrometerTracingDecorator(tracer));

        return executor;
    }

    // =========================================================
    // 2. 默认的异步配置 (可被覆盖)
    // =========================================================

    /**
     * 只有当容器中没有其他 AsyncConfigurer 时，才注册我们的全局异常处理器配置。 这解决了 "Only one AsyncConfigurer may exist" 的问题。
     */
    @Configuration
    @ConditionalOnMissingBean(AsyncConfigurer.class)
    static class DefaultAsyncConfigurer implements AsyncConfigurer {

        @Resource
        Tracer tracer;

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return (ex, method, params) -> {
                // 💡 这里的日志会自动带上 TraceID 和 SpanID（因为装饰器已经初始化了上下文）
                logger.error("❌ 异步任务执行异常! 方法: {}, 参数: {}, 异常信息: {}", method.getName(), params, ex.getMessage(), ex);
                Span span = tracer.currentSpan();
                if (span != null) {
                    span.error(ex); // 显式标记 Span 失败
                }
            };
        }
    }

    @Data
    @NoArgsConstructor
    public static class BaseConfig {
        private String threadNamePrefix;
    }

    @Data
    @NoArgsConstructor
    public static class VtAsyncConfig extends BaseConfig {
        private Integer concurrencyLimit;
    }

    @Data
    @NoArgsConstructor
    public static class PtAsyncConfig extends BaseConfig {
        private Integer corePoolSize;
        private Integer maxPoolSize;
        private Integer queueCapacity;
        private String rejectedExecHandlerType;
        private RejectedExecutionHandler rejectedExecutionHandler;

        public RejectedExecutionHandler getRejectedExecHandler() {
            if (null != rejectedExecutionHandler) {
                return rejectedExecutionHandler;
            }
            switch (rejectedExecHandlerType) {
                case "CallerRunsPolicy":
                    rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                    break;
                case "DiscardPolicy":
                    rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                    break;
                case "DiscardOldestPolicy":
                    rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
                    break;
                case "AbortPolicy":
                default:
                    rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
            }
            return rejectedExecutionHandler;
        }
    }
}