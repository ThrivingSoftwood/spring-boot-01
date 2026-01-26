package thriving.softwood.common.framework.component.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskDecorator;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.NonNull;

/**
 * 🚀 Micrometer 嵌套链路追踪装饰器 (Spring Boot 4 Standard)
 * <p>
 * 作用： 1. 解决跨线程 TraceContext 丢失问题。 2. 显式创建 Child Span，实现精确的 [Parent -> Child] 追踪关系。 3. 输出线程切换的衔接日志，便于排查并发问题。
 *
 * @author ThrivingSoftwood
 * @since 2026-01-26
 */
public class MicrometerTracingDecorator implements TaskDecorator {

    private static final Logger logger = LoggerFactory.getLogger(MicrometerTracingDecorator.class);

    private final Tracer tracer;

    public MicrometerTracingDecorator(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        // 1. 【父线程】获取当前上下文中的 Span (可能为空)
        Span parentSpan = tracer.currentSpan();

        // 2. 【父线程】基于当前上下文创建新的子 Span (Child Span)
        // .nextSpan() 会自动检测当前上下文，如果有 parent 则关联，没有则作为 root
        Span childSpan = tracer.nextSpan().name("async-task");

        // 3. 【父线程】构建衔接日志 (Requirement: [pSpanId -> spanId])
        String parentId = (parentSpan != null) ? parentSpan.context().spanId() : "root";
        String childId = childSpan.context().spanId();

        // 记录此时发生的线程派发行为
        // 注意：此时 Logger MDC 依然是 Parent 的上下文
        logger.info("🧵 Thread Dispatch: [{} -> {}] Task submitted.", parentId, childId);

        // 4. 返回包装后的 Runnable
        return () -> {
            // 5. 【子线程】启动 Span 并注入当前线程的 ThreadLocal/MDC
            try (Tracer.SpanInScope ws = tracer.withSpan(childSpan.start())) {
                // 此时 MDC 已被 Micrometer 自动更新为 traceId + childSpanId
                runnable.run();
            } finally {
                // 6. 【子线程】结束 Span，上报数据
                childSpan.end();
            }
        };
    }
}