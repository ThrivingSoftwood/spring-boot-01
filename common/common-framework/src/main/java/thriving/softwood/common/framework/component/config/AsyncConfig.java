package thriving.softwood.common.framework.component.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import thriving.softwood.common.framework.component.decorator.MdcTaskDecorator;

/**
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean
    public Executor ptExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 依据机器核数设定：CPU核数 + 1
        int cores = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(cores + 1);
        executor.setMaxPoolSize(cores * 2);

        // 队列不宜过大，防止积压导致内存溢出
        executor.setQueueCapacity(500);

        // 🔥 关键：挂载装饰器
        executor.setTaskDecorator(new MdcTaskDecorator());
        // ⚠️ 生产级必选：拒绝策略。当池子满时，由调用者线程执行（此时装饰器依然生效）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor vtExecutor() {
        // TODO: 虚拟线程不限制并发数,但是下游组件承压能力存在极限,需要在业务层或 AOP 层通过 Resilience4j 或 Guava Semaphore 来保护下游
        // 1. 使用支持虚拟线程的执行器
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

        // 2. 开启虚拟线程模式 (JDK 21+)
        executor.setVirtualThreads(true);

        // 3. 挂载我们之前的 MDC 装饰器
        // 逻辑完全复用：捕获主线程 MDC，注入虚拟线程，生成子任务 SpanID
        executor.setTaskDecorator(new MdcTaskDecorator());

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

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return (ex, method, params) -> {
                // 💡 这里的日志会自动带上 TraceID 和 SpanID（因为装饰器已经初始化了上下文）
                logger.error("❌ 异步任务执行异常! 方法: {}, 参数: {}, 异常信息: {}", method.getName(), params, ex.getMessage(), ex);
            };
        }
    }
}