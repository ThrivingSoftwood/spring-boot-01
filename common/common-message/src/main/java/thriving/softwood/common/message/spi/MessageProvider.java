package thriving.softwood.common.message.spi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import thriving.softwood.common.framework.annotation.async.VtAsync;
import thriving.softwood.common.message.enums.MessageTypeEnum;
import thriving.softwood.common.message.infrastructure.db.master.entity.base.SysMessage;

/**
 * 消息中心业务服务
 * 
 * @author CodeOmni
 */
public interface MessageProvider {

    @VtAsync
    void saveAndPushAsync(Long userId, MessageTypeEnum msgType, String msg, String bizRefId);

    /**
     * 🚀 核心方法：异步落库并推送消息 使用 @VtAsync 虚拟线程，确保该操作不会阻塞主业务（如订单状态更新操作）
     *
     * @param msg 构造好的消息实体
     */
    @VtAsync
    void saveAndPushAsync(SysMessage msg);

    /**
     * 获取当前用户的未读消息总数
     */
    Long getUnreadCount();

    /**
     * 获取当前用户的历史消息 (分页)
     */
    Page<SysMessage> pageMessages(long pageNo, long pageSize, Byte isRead);

    /**
     * 标记单条消息为已读
     */
    void markAsRead(Long messageId);

    /**
     * 标记全部未读消息为已读 (一键已读)
     */
    void markAllAsRead();
}