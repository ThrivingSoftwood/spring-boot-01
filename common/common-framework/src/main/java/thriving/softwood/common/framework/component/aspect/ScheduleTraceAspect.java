package thriving.softwood.common.framework.component.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

/**
 * 自动追踪切面 (增强版)
 * <p>
 * 职责： 1. 定时任务：负责创建并命名 Span (因为定时任务没有上层 Span)。 2. 异步任务 (@PtAsync/@VtAsync)：负责对 Decorator 创建的 Span 进行【重命名】，填补类名信息。
 * </p>
 */
@Aspect
@Component
@Order(1)
public class ScheduleTraceAspect {

    private final Tracer tracer;

    public ScheduleTraceAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    // 匹配定时任务
    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled) || "
        + "@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void schedulePointcut() {}

    @Around("schedulePointcut()")
    public Object traceSchedule(ProceedingJoinPoint joinPoint) throws Throwable {
        // 定时任务逻辑保持不变：因为它没有上层线程，需要在这里 nextSpan()
        String spanName =
            joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName();

        Span newSpan = tracer.nextSpan().name(spanName);
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {
            return joinPoint.proceed();
        } finally {
            newSpan.end();
        }
    }
}