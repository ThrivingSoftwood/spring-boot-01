package thriving.softwood.common.core.enums;

import static thriving.softwood.common.core.constant.LogMsgConstant.*;
import static thriving.softwood.common.core.constant.PunctuationConstants.HYPHEN;

/**
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
public enum ThreadNamePrefixEnum {

    SPT(SYNC_NAME_PREFIX, "服务被触发时的初始同步线程,肯定为平台线程"),
    PMT("p", "平台多线程(Platform Threads),当任务是 CPU 密集型（如加密、数学运算）时使用,虚拟线程在这种场景下反而会因为上下文切换（虽然轻量）而变慢。"),
    VMT("v", "虚拟多线程(Virtual Thread),当任务是 IO 密集型（如查数据库、调 API、写文件）时使用.,占用资源远低于平台线程"),
    STS(SUBTHREAD_NAME_PREFIX, "开启多线程后线程池中的子线程,可能是虚拟线程,也可能是平台线程");

    ThreadNamePrefixEnum(String mark, String desc) {
        switch (mark) {
            case SYNC_NAME_PREFIX:
                threadMark = SYNC_NAME_PREFIX;
                break;
            case SUBTHREAD_NAME_PREFIX:
                threadMark = SUBTHREAD_NAME_PREFIX;
                break;
            case "p":
            case "v":
                threadMark = ASYNC_NAME_PREFIX + HYPHEN + mark;
                break;
            default:
                throw new UnsupportedOperationException("ThreadNamePrefixEnum 的错误初始化:参数 mark 值非法:" + mark);
        }
    }

    /**
     * 主线程标识
     */
    private final String threadMark;

    public String mark() {
        return threadMark;
    }
}
