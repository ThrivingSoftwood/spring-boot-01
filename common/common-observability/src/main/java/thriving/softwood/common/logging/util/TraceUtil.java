package thriving.softwood.common.logging.util;

import static thriving.softwood.common.core.constant.PunctuationConstant.HYPHEN;
import static thriving.softwood.common.core.constant.TraceConstant.*;
import static thriving.softwood.common.core.enums.ThreadNamePrefixEnum.SPT;
import static thriving.softwood.common.core.enums.ThreadNamePrefixEnum.STS;
import static thriving.softwood.common.core.util.StringUtil.getLastPart;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.text.StrUtil;

/**
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
public class TraceUtil {

    private static final Logger logger = LoggerFactory.getLogger(TraceUtil.class);

    /**
     * 开启追踪
     *
     * @param traceId 外部传入的 ID，如果为空则自动生成
     *
     * @return 当前使用的 traceId
     */
    public static String start(String traceId) {
        if (StrUtil.isBlank(traceId)) {
            traceId = SPT.mark() + HYPHEN + generateTraceId();
        }
        MDC.put(TRACE_ID_KEY, traceId);
        MDC.put(SPAN_ID_KEY, MAIN_SPAN_ID);
        return traceId;
    }

    /**
     * 为子线程生成上下文（由装饰器调用）
     * 
     * @param contextMap 父线程的 MDC 内容
     * @param threadMark 主线程标识(main thread mark)
     */
    public static void applyContext(Map<String, String> contextMap, String threadMark) {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
        // 🚀 核心逻辑：区分是否为多线程/多线程类型,需要增加前缀. SPT : Sync Platform Thread
        MDC.put(TRACE_ID_KEY, threadMark + HYPHEN + getLastPart(MDC.get(TRACE_ID_KEY), HYPHEN));

        String parentId = MDC.get(SPAN_ID_KEY);
        if (StrUtil.isBlank(parentId)) {
            // 🚀 核心逻辑：即使复制了父线程，也要给子线程一个独一无二的 SpanID; STS : Sub Threads
            MDC.put(SPAN_ID_KEY, STS.mark() + HYPHEN + IdUtil.getSnowflakeNextIdStr());
        } else {
            String childId = STS.mark() + HYPHEN + IdUtil.getSnowflakeNextIdStr();
            // 记录此时发生的线程派发行为
            // 注意：此时 Logger MDC 依然是 Parent 的上下文
            logger.info("🧵 Thread Dispatch: [{} -> {}] Task submitted.", parentId, childId);
            MDC.put(PARENT_SPAN_ID_KEY, parentId);
            MDC.put(SPAN_ID_KEY, childId);
        }
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
