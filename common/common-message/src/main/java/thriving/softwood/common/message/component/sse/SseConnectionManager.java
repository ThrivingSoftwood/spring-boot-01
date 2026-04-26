package thriving.softwood.common.message.component.sse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import thriving.softwood.common.framework.annotation.async.VtAsync;

/**
 * 🚀 生产级 SSE 连接池管理器 (多端并发广播版) 负责管理用户多设备连接、断开清理、消息下发及心跳保活。
 *
 * @author ThrivingSoftwood
 */
@Component
public class SseConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(SseConnectionManager.class);

    // 🌟 核心升级：Value 改为线程安全的 CopyOnWriteArraySet，支持单用户多设备/多标签页同时在线
    private final Map<Long, CopyOnWriteArraySet<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    /**
     * 创建并注册 SSE 连接 (支持多端)
     */
    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(0L); // 0L 表示永不超时，由 Nginx 或前端主动断开

        // 原子操作：如果该用户是第一次连，初始化一个 Set；否则获取现有的 Set
        CopyOnWriteArraySet<SseEmitter> userEmitters =
            emitterMap.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>());

        userEmitters.add(emitter);

        logger.info("📡 用户 [{}] 新增一个连接。当前该用户会话数: {}，总在线用户数: {}", userId, userEmitters.size(), emitterMap.size());

        // 统一的连接清理回调任务
        Runnable removeTask = () -> {
            userEmitters.remove(emitter);
            // 内存保护：如果该用户的所有端都下线了，将用户的 key 从 Map 中彻底移除
            if (userEmitters.isEmpty()) {
                emitterMap.remove(userId);
            }
            logger.info("🔌 用户 [{}] 的一个 SSE 连接已断开，剩余会话数: {}", userId, userEmitters.size());
        };

        // 注册生命周期回调
        emitter.onCompletion(removeTask);
        emitter.onTimeout(removeTask);
        emitter.onError((e) -> {
            logger.error("❌ 用户 [{}] SSE 连接异常: {}", userId, e.getMessage());
            removeTask.run();
        });

        // 发送确认消息 (ACK)
        try {
            emitter.send(SseEmitter.event().name("connect").data("ACK"));
        } catch (IOException e) {
            removeTask.run();
        }

        return emitter;
    }

    /**
     * 核心推送方法：向指定用户的所有在线设备广播消息
     */
    public void sendMessage(Long userId, Object messageObj) {
        CopyOnWriteArraySet<SseEmitter> userEmitters = emitterMap.get(userId);

        if (userEmitters != null && !userEmitters.isEmpty()) {
            for (SseEmitter emitter : userEmitters) {
                try {
                    // 推送自定义事件 name("message")
                    emitter.send(SseEmitter.event().name("message").data(messageObj));
                } catch (IOException e) {
                    // 某一个端推送失败（比如浏览器直接被杀掉），清理该无效的 Emitter
                    logger.warn("⚠️ 向用户 [{}] 的某一个端推送失败，清理该闲置连接", userId);
                    userEmitters.remove(emitter);
                }
            }

            // 如果所有端都推送失败并被移除了，清理外层 Map
            if (userEmitters.isEmpty()) {
                emitterMap.remove(userId);
            }
        }
    }

    /**
     * 💓 守护线程：心跳保活任务 每 45 秒向所有在线用户的【所有设备】发送 ping，防止网关掐断。
     */
    @Scheduled(fixedRate = 45000)
    @VtAsync
    public void sendHeartbeat() {
        if (emitterMap.isEmpty()) {
            return;
        }

        logger.debug("💓 开始发送 SSE 心跳，当前在线用户数: {}", emitterMap.size());

        emitterMap.forEach((userId, userEmitters) -> {
            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
                } catch (IOException e) {
                    userEmitters.remove(emitter);
                }
            }
            // 清理空会话的用户
            if (userEmitters.isEmpty()) {
                emitterMap.remove(userId);
            }
        });
    }
}