package thriving.softwood.common.message.component.listener;

import static thriving.softwood.common.message.enums.MessageTypeEnum.RUNTIME_ERROR;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import thriving.softwood.common.core.event.SystemErrorEvent;
import thriving.softwood.common.framework.annotation.async.VtAsync;
import thriving.softwood.common.message.infrastructure.db.master.entity.base.SysMessage;
import thriving.softwood.common.message.spi.MessageProvider;
import thriving.softwood.common.security.spi.UserAuthProvider;

/**
 * 🎧 异常事件监听器 属于 common-message 模块，它可以自由调用 MessageProvider 和 SysMessage
 */
@Component
public class SystemErrorListener {

    private final MessageProvider messageProvider;

    private final UserAuthProvider userProvider;

    public SystemErrorListener(MessageProvider messageProvider, UserAuthProvider userProvider) {
        this.messageProvider = messageProvider;
        this.userProvider = userProvider;
    }

    /**
     * 🚀 监听到异常事件后的处理逻辑 使用 @VtAsync 虚拟线程异步执行，绝不卡住异常响应
     */
    @EventListener
    @VtAsync
    public void onSystemError(SystemErrorEvent event) {
        SysMessage errorMsg = new SysMessage();
        errorMsg.setReceiverId(null == event.getUserId() ? 1L : event.getUserId());
        errorMsg.setMsgType(RUNTIME_ERROR.typeCode()); // 运行异常
        errorMsg.setReadStatus((byte)0);
        errorMsg.setTitle("操作执行异常通知");

        StringBuilder sb = new StringBuilder();
        sb.append("请求地址：").append(event.getRequestUri()).append("\n");
        sb.append("异常类型：").append(event.getThrowable().getClass().getSimpleName()).append("\n");
        sb.append("报错内容：").append(event.getThrowable().getMessage());

        errorMsg.setContent(sb.toString());
        errorMsg.setBizRefId("ERR_" + System.currentTimeMillis());

        // 执行持久化与推送
        messageProvider.saveAndPushAsync(errorMsg);
    }
}