package thriving.softwood.common.framework.annotation;

import java.lang.annotation.*;

import org.springframework.scheduling.annotation.Async;

/**
 * 🚀 虚拟线程异步执行注解 适用于：高并发、IO 密集型任务 映射至：AsyncConfig 中的 vtExecutor
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Async("vtExecutor") // 指向具体的 Bean 名称
public @interface VtAsync {}