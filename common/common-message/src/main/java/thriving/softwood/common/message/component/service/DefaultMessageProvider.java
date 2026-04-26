package thriving.softwood.common.message.component.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.common.framework.annotation.async.VtAsync;
import thriving.softwood.common.message.component.sse.SseConnectionManager;
import thriving.softwood.common.message.enums.MessageTypeEnum;
import thriving.softwood.common.message.infrastructure.db.master.entity.base.SysMessage;
import thriving.softwood.common.message.infrastructure.db.master.repo.SysMessageRepo;
import thriving.softwood.common.message.spi.MessageProvider;
import thriving.softwood.common.security.context.UserContext;

/**
 * 消息中心业务服务
 * 
 * @author CodeOmni
 */
@Service
@DS("master") // SysMessage 存在 ts_auth 库中
public class DefaultMessageProvider implements MessageProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageProvider.class);

    private final SysMessageRepo sysMessageRepo;
    private final SseConnectionManager sseConnectionManager;

    public DefaultMessageProvider(SysMessageRepo sysMessageRepo, SseConnectionManager sseConnectionManager) {
        this.sysMessageRepo = sysMessageRepo;
        this.sseConnectionManager = sseConnectionManager;
    }

    /**
     * 🚀 核心方法：异步落库并推送消息 使用 @VtAsync 虚拟线程，确保该操作不会阻塞主业务（如订单状态更新操作）
     *
     * @param msg 构造好的消息实体
     */
    @VtAsync
    @Override
    public void saveAndPushAsync(Long userId, MessageTypeEnum msgType, String msg, String bizRefId) {

        SysMessage msgObj = new SysMessage();
        msgObj.setReceiverId(userId);
        msgObj.setMsgType(msgType.typeCode());
        msgObj.setReadStatus((byte)0);
        msgObj.setTitle(msgType.typeDesc());
        msgObj.setContent(msg);
        msgObj.setBizRefId(StrUtil.isBlank(bizRefId) ? "ERR_" + System.currentTimeMillis() : bizRefId);
        try {
            // 1. 持久化落库 (存证)
            // MyBatis-Plus 的自动填充器会为你生成 createTime
            sysMessageRepo.save(msgObj);

            // 2. 实时推送 (通知)
            // 只要落库成功，我们就可以将携带了完整 ID 的消息对象推送给前端
            sseConnectionManager.sendMessage(msgObj.getReceiverId(), msg);

            logger.info("📩 消息预警已落库并尝试下发给用户 [{}]", msgObj.getReceiverId());

        } catch (Exception e) {
            // 注意：消息发送失败绝不能影响主业务逻辑，必须捕获并仅记录日志
            logger.error("🚨 消息持久化或推送失败，接收人: {}", msgObj.getReceiverId(), e);
        }
    }

    /**
     * 🚀 核心方法：异步落库并推送消息 使用 @VtAsync 虚拟线程，确保该操作不会阻塞主业务（如订单状态更新操作）
     *
     * @param msg 构造好的消息实体
     */
    @VtAsync
    @Override
    public void saveAndPushAsync(SysMessage msg) {
        try {
            // 1. 持久化落库 (存证)
            // MyBatis-Plus 的自动填充器会为你生成 createTime
            sysMessageRepo.save(msg);

            // 2. 实时推送 (通知)
            // 只要落库成功，我们就可以将携带了完整 ID 的消息对象推送给前端
            sseConnectionManager.sendMessage(msg.getReceiverId(), msg);

            logger.info("📩 消息预警已落库并尝试下发给用户 [{}]", msg.getReceiverId());

        } catch (Exception e) {
            // 注意：消息发送失败绝不能影响主业务逻辑，必须捕获并仅记录日志
            logger.error("🚨 消息持久化或推送失败，接收人: {}", msg.getReceiverId(), e);
        }
    }

    /**
     * 获取当前用户的未读消息总数
     */
    @Override
    public Long getUnreadCount() {
        Long userId = UserContext.userId();
        return sysMessageRepo.countUnread(userId);
    }

    /**
     * 获取当前用户的历史消息 (分页)
     */
    @Override
    public Page<SysMessage> pageMessages(long pageNo, long pageSize, Byte isRead) {
        Long userId = UserContext.userId();

        return sysMessageRepo.pageMessages(userId, pageNo, pageSize, isRead);
    }

    /**
     * 标记单条消息为已读
     */
    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId) {
        Long userId = UserContext.userId();
        sysMessageRepo.markAsRead(userId, messageId);
    }

    /**
     * 标记全部未读消息为已读 (一键已读)
     */
    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        Long userId = UserContext.userId();
        sysMessageRepo.markAllAsRead(userId);
    }
}