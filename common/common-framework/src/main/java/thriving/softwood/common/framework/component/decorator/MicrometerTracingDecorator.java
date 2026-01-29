package thriving.softwood.common.framework.component.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskDecorator;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 🚀 Micrometer 链路追踪装饰器 (高性能衔接日志版)
 * <p>
 * 优化点： 1. 【保留】Child Span: 保证 Zipkin 并发视图。 2. 【核心】衔接日志: 记录线程切换点，方便离线排查。 3. 【高性能】惰性日志: 仅在 INFO 级别开启时获取 ID，避免无谓的内存分配和方法调用。
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-01-29
 */
@RequiredArgsConstructor
public class MicrometerTracingDecorator implements TaskDecorator {

    private static final Logger logger = LoggerFactory.getLogger(MicrometerTracingDecorator.class);
    private final Tracer tracer;

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        // 1. 获取父线程 Span
        Span parentSpan = tracer.currentSpan();

        // 2. 快速失败：如果没有 Trace 上下文，直接返回原任务（零开销）
        if (parentSpan == null) {
            return runnable;
        }

        // 3. 创建子 Span (保持 Zipkin 链路分叉)
        Span childSpan = tracer.nextSpan().name("async-task");

        // 4. 【高性能衔接日志】
        // 只有在开启了 INFO 级别时，才去解析 SpanContext 获取 ID。
        // 在生产环境关闭或调高日志级别时，此块逻辑的 CPU 开销几乎为 0。
        if (logger.isInfoEnabled()) {
            // 使用占位符模式，避免字符串拼接
            logger.info("🧵 Dispatch: [{} -> {}] Context propagated.", parentSpan.context().spanId(),
                childSpan.context().spanId());
        }

        // 5. 任务包装
        return () -> {
            try (Tracer.SpanInScope ws = tracer.withSpan(childSpan.start())) {
                runnable.run();
            } finally {
                childSpan.end();
            }
        };
    }
}