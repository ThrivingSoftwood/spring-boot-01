package thriving.softwood.common.logging.component.decorator;

import static thriving.softwood.common.core.enums.ThreadNamePrefixEnum.PT;
import static thriving.softwood.common.core.enums.ThreadNamePrefixEnum.VT;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import thriving.softwood.common.logging.util.TraceUtil;

/**
 * 📘 MDC 任务装饰器 作用：实现父子线程间日志上下文的丝滑传递
 */
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 1. 【此时在父线程】获取当前 MDC 的副本
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {
            try {
                // 2. 【此时在子线程】将父线程的上下文注入，并生成新的 SpanID
                TraceUtil.applyContext(contextMap, Thread.currentThread().isVirtual() ? VT.stPrefix() : PT.stPrefix());
                // 3. 执行真正的业务逻辑
                runnable.run();
            } finally {
                // 4. 【重要】清理子线程上下文，防止线程池回收线程后导致日志串号
                TraceUtil.end();
            }
        };
    }
}