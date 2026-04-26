package thriving.softwood.common.core.event;

import java.io.Serial;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * 🚀 系统异常事件 (用于模块间解耦)
 */
@Getter
public class SystemErrorEvent extends ApplicationEvent {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Throwable throwable;
    private final String requestUri;
    // 可以在发布时带上，也可以让监听者自己去 Context 拿
    private final Long userId;

    public SystemErrorEvent(Long userId, Object source, Throwable throwable, String requestUri) {
        super(source);
        this.throwable = throwable;
        this.requestUri = requestUri;
        this.userId = userId;
    }
}