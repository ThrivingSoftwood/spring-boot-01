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
@Order()
public class AsyncTraceAspect {

    private final Tracer tracer;

    public AsyncTraceAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    // 匹配异步任务
    @Pointcut("@annotation(thriving.softwood.common.framework.annotation.async.PtAsync) || "
        + "@annotation(thriving.softwood.common.framework.annotation.async.VtAsync)")
    public void asyncTaskPointcut() {}

    @Around("asyncTaskPointcut()")
    public Object traceAsync(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取由 Decorator 刚刚挂载到当前线程的 Span
        Span currentSpan = tracer.currentSpan();

        if (currentSpan != null) {
            // 2. 精准重命名：从占位符 "async-operation" 改为 "OrderService.saveOrder"
            String realName =
                joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName();
            currentSpan.name(realName);
        }

        return joinPoint.proceed();
    }

}