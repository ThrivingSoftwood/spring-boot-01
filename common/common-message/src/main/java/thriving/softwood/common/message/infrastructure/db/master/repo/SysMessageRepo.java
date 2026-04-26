package thriving.softwood.common.message.infrastructure.db.master.repo;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.common.message.infrastructure.db.master.entity.base.SysMessage;
import thriving.softwood.common.message.infrastructure.db.master.mapper.base.SysMessageMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("master")
@Service
public class SysMessageRepo extends AncestorServiceImpl<SysMessageMapper, SysMessage> {

    /**
     * 获取当前用户的未读消息总数
     */
    public Long countUnread(Long userId) {
        return lambdaQuery().eq(SysMessage::getReceiverId, userId).eq(SysMessage::getReadStatus, 0).count();
    }

    /**
     * 获取当前用户的历史消息 (分页)
     */
    public Page<SysMessage> pageMessages(Long userId, long pageNo, long pageSize, Byte isRead) {
        Page<SysMessage> page = new Page<>(pageNo, pageSize);
        return lambdaQuery().eq(SysMessage::getReceiverId, userId).eq(isRead != null, SysMessage::getReadStatus, isRead)
            .orderByDesc(SysMessage::getCreateTime).page(page);
    }

    /**
     * 标记单条消息为已读
     */
    public void markAsRead(Long userId, Long messageId) {
        // 严防越权操作别人的消息
        lambdaUpdate().eq(SysMessage::getId, messageId).eq(SysMessage::getReceiverId, userId)
            .eq(SysMessage::getReadStatus, 0).set(SysMessage::getReadStatus, 1)
            .set(SysMessage::getReadTime, LocalDateTime.now()).update();
    }

    /**
     * 标记全部未读消息为已读 (一键已读)
     */
    public void markAllAsRead(Long userId) {
        lambdaUpdate().eq(SysMessage::getReceiverId, userId).eq(SysMessage::getReadStatus, 0)
            .set(SysMessage::getReadStatus, 1).set(SysMessage::getReadTime, LocalDateTime.now()).update();
    }
}
