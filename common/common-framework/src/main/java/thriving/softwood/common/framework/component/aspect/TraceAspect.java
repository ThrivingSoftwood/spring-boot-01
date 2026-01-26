package thriving.softwood.common.framework.component.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

/**
 * 自动追踪切面 补充 Spring Boot 自动配置可能未覆盖的场景 (如 XXL-JOB, 自定义定时任务等)
 *
 * @author ThrivingSoftwood
 * @since 2026-01-26
 */
@Aspect
@Component
@Order(1)
public class TraceAspect {

    private final Tracer tracer;

    public TraceAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled) || "
        + "@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public Object traceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 创建一个新的 Span
        Span newSpan = tracer.nextSpan().name(joinPoint.getSignature().getName());

        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {
            return joinPoint.proceed();
        } finally {
            newSpan.end();
        }
    }
}