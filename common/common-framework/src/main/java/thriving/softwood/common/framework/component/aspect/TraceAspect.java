package thriving.softwood.common.framework.component.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.common.logging.util.TraceUtil;

/**
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
@Aspect
@Component
@Order(1) // 切面优先级，越小越早执行
public class TraceAspect {

    /**
     * 定义切点：覆盖主流的后台任务入口 1. @Scheduled (Spring Task) 2. @KafkaListener (Kafka) 3. @RabbitListener (RabbitMQ)
     * 4. @RocketMQMessageListener (RocketMQ) 5. @XxlJob (XXL-JOB)
     */
    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled) || "
        + "@annotation(org.springframework.kafka.annotation.KafkaListener) || "
        + "@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener) || "
        + "@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public Object traceAround(ProceedingJoinPoint joinPoint) throws Throwable {

        // 判断当前是否已经存在 TraceID (防止 AOP 嵌套调用或者从 Web 层调用了 Service)
        boolean isRoot = false;
        if (StrUtil.isBlank(TraceUtil.getTraceId())) {
            // 如果没有，说明我是源头，生成一个新的
            // TODO: 如果是 MQ，这里可以进一步解析 args 获取 Message Header 里的 traceId 实现透传
            TraceUtil.start(null);
            isRoot = true;
        }

        try {
            return joinPoint.proceed();
        } finally {
            // 只有我是源头时，我才负责清理
            // 如果我只是嵌套调用的一环，清理工作交给上层 (比如 Filter 或 外层 AOP)
            if (isRoot) {
                TraceUtil.end();
            }
        }
    }
}