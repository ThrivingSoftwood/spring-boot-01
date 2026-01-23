package thriving.softwood.common.framework.annotation;

import java.lang.annotation.*;

import org.springframework.scheduling.annotation.Async;

/**
 * 🧱 平台线程池异步执行注解 适用于：CPU 密集型任务、需要控制并发上限的任务 映射至：AsyncConfig 中的 ptExecutor
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Async("ptExecutor") // 指向具体的 Bean 名称
public @interface PtAsync {}