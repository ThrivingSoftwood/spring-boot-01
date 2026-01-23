package thriving.softwood.common.core.enums;

import static thriving.softwood.common.core.constant.LogMsgConstant.MULTITHREAD_NAME_PREFIX;
import static thriving.softwood.common.core.constant.LogMsgConstant.SUBTHREAD_NAME_PREFIX;
import static thriving.softwood.common.core.constant.PunctuationConstants.HYPHEN;

/**
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
public enum ThreadNamePrefixEnum {
    VT("v", "虚拟线程(Virtual Thread),当任务是 IO 密集型（如查数据库、调 API、写文件）时使用."),
    PT("p", "平台线程(Platform Threads),当任务是 CPU 密集型（如加密、数学运算）时使用,虚拟线程在这种场景下反而会因为上下文切换（虽然轻量）而变慢。");

    ThreadNamePrefixEnum(String mark, String desc) {
        mainThreadPrefix = MULTITHREAD_NAME_PREFIX + HYPHEN + mark + HYPHEN;
        subThreadPrefix = SUBTHREAD_NAME_PREFIX + HYPHEN + mark + HYPHEN;
    }

    private final String mainThreadPrefix;
    private final String subThreadPrefix;

    public String mtPrefix() {
        return mainThreadPrefix;
    }

    public String stPrefix() {
        return subThreadPrefix;
    }
}
