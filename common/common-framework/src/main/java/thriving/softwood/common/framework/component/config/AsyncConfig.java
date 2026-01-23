package thriving.softwood.common.framework.component.config;

import static thriving.softwood.common.core.enums.ThreadNamePrefixEnum.PT;
import static thriving.softwood.common.core.enums.ThreadNamePrefixEnum.VT;

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

import thriving.softwood.common.logging.component.decorator.MdcTaskDecorator;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean
    public Executor ptExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setThreadNamePrefix(PT.mtPrefix());

        // 🔥 关键：挂载装饰器
        executor.setTaskDecorator(new MdcTaskDecorator());
        // ⚠️ 生产级必选：拒绝策略。当池子满时，由调用者线程执行（此时装饰器依然生效）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor vtExecutor() {
        // 1. 使用支持虚拟线程的执行器
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor(VT.mtPrefix());

        // 2. 开启虚拟线程模式 (JDK 21+)
        executor.setVirtualThreads(true);

        // 3. 挂载我们之前的 MDC 装饰器
        // 逻辑完全复用：捕获主线程 MDC，注入虚拟线程，生成子任务 SpanID
        executor.setTaskDecorator(new MdcTaskDecorator());

        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            // 💡 这里的日志会自动带上 TraceID 和 SpanID（因为装饰器已经初始化了上下文）
            logger.error("❌ 异步任务执行异常! 方法: {}, 参数: {}, 异常信息: {}", method.getName(), params, ex.getMessage(), ex);
        };
    }
}