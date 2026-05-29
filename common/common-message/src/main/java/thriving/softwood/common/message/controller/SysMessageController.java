package thriving.softwood.common.message.controller;

import static thriving.softwood.common.message.enums.MessageTypeEnum.BIZ_WARNING;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.common.message.component.sse.SseConnectionManager;
import thriving.softwood.common.message.infrastructure.db.master.entity.base.SysMessage;
import thriving.softwood.common.message.spi.MessageProvider;
import thriving.softwood.common.security.context.UserContext;

/**
 * 实时消息与预警中心入口
 *
 * @author CodeOmni
 */
@RestController
@RequestMapping("/message")
public class SysMessageController {

    private final SseConnectionManager sseConnectionManager;
    private final MessageProvider messageProvider;

    public SysMessageController(SseConnectionManager sseConnectionManager, MessageProvider messageProvider) {
        this.sseConnectionManager = sseConnectionManager;
        this.messageProvider = messageProvider;
    }

    /**
     * 📊 2. 获取当前未读数量 (初始化小铃铛)
     */
    @GetMapping("/manual")
    public Result<Long> manualMessage() {
        messageProvider.saveAndPushAsync(1L, BIZ_WARNING, "测试通知功能", null);
        return Result.success(1L);
    }

    /**
     * 🎧 1. 客户端订阅实时消息通道 (SSE 长连接) 注意：这里产生的返回值类型为 TEXT_EVENT_STREAM_VALUE，它不会立即结束请求！
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        Long userId = UserContext.userId();
        if (userId == null) {
            throw new RuntimeException("非法访问，未获取到用户身份！");
        }
        // 交由底层多端连接池接管
        return sseConnectionManager.connect(userId);
    }

    /**
     * 📊 2. 获取当前未读数量 (初始化小铃铛)
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        return Result.success(messageProvider.getUnreadCount());
    }

    /**
     * 📜 3. 分页获取历史消息
     */
    @GetMapping("/page")
    public Result<Page<SysMessage>> pageMessages(@RequestParam(defaultValue = "1") long pageNo,
        @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Byte isRead) {
        return Result.success(messageProvider.pageMessages(pageNo, pageSize, isRead));
    }

    /**
     * ✅ 4. 标记单条已读
     */
    @PutMapping("/read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id) {
        messageProvider.markAsRead(id);
        return Result.success();
    }

    /**
     * ✅ 5. 全部一键已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        messageProvider.markAllAsRead();
        return Result.success();
    }
}