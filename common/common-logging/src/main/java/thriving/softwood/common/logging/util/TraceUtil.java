package thriving.softwood.common.logging.util;

import java.util.Map;

import org.slf4j.MDC;

import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.text.StrUtil;

/**
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
public class TraceUtil {

    /**
     * 日志配置文件中引用的 Key
     */
    public static final String TRACE_ID_KEY = "traceId";

    // 多线程相关 begin
    // 子线程标识 ID 的 key
    public static final String SPAN_ID_KEY = "spanId";
    public static final String MAIN_SPAN_ID = "main";

    /**
     * HTTP Header 中的 Key (用于跨服务透传)
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * 开启追踪
     *
     * @param traceId 外部传入的 ID，如果为空则自动生成
     *
     * @return 当前使用的 traceId
     */
    public static String start(String traceId) {
        if (StrUtil.isBlank(traceId)) {
            traceId = generateTraceId();
        }
        MDC.put(TRACE_ID_KEY, traceId);
        MDC.put(SPAN_ID_KEY, MAIN_SPAN_ID);
        return traceId;
    }

    /**
     * 为子线程生成上下文（由装饰器调用）
     * 
     * @param contextMap 父线程的 MDC 内容
     */
    public static void applyContext(Map<String, String> contextMap, String stPrefix) {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
        // 🚀 核心逻辑：即使复制了父线程，也要给子线程一个独一无二的 SpanID
        // 使用 4 位简短随机码，既区分了线程，又不占用过多日志空间，对新手极其友好
        MDC.put(SPAN_ID_KEY, stPrefix + IdUtil.getSnowflakeNextIdStr());
    }

    /**
     * 结束追踪，清理上下文
     */
    public static void end() {
        MDC.clear();
    }

    /**
     * 获取当前 TraceID
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * 生成标准 TraceID (去横线 UUID)
     */
    public static String generateTraceId() {
        return IdUtil.getSnowflakeNextIdStr();
    }
}
