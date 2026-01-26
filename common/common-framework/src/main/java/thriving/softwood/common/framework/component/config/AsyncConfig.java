package thriving.softwood.common.framework.component.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.micrometer.tracing.Tracer;
import thriving.softwood.common.framework.component.decorator.MicrometerTracingDecorator;

/**
 * 异步线程池配置 (Micrometer Native)
 *
 * @author ThrivingSoftwood
 * @since 2026-01-26
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    /**
     * 🧱 平台线程池：适用于 CPU 密集型任务
     */
    @Bean("ptExecutor")
    public Executor ptExecutor(Tracer tracer) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("pt-exec-");

        // 核心：挂载 Micrometer 装饰器
        executor.setTaskDecorator(new MicrometerTracingDecorator(tracer));

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 🚀 虚拟线程池：适用于 IO 密集型任务 (JDK 21+)
     */
    @Bean("vtExecutor")
    public Executor vtExecutor(Tracer tracer) {
        // 使用 SimpleAsyncTaskExecutor 并开启虚拟线程支持
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("vt-exec-");
        executor.setVirtualThreads(true);

        // 核心：同样挂载装饰器，确保虚拟线程也能传递 Trace
        executor.setTaskDecorator(new MicrometerTracingDecorator(tracer));

        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            // Micrometer 会自动将 TraceContext 注入到 MDC，这里的日志会自动带上 ID
            logger.error("❌ Async Exception in method: {}", method.getName(), ex);
        };
    }
}