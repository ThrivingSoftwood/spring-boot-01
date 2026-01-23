package thriving.softwood.simple.api;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import thriving.softwood.common.framework.annotation.async.PtAsync;
import thriving.softwood.common.framework.annotation.async.VtAsync;

/**
 * 多线程示例
 * 
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
@Service
public class AncestorAsyncSvc implements AncestorAsyncApi {

    private static final Logger logger = LoggerFactory.getLogger(AncestorAsyncSvc.class);

    /**
     * 🚀 模拟 I/O 密集型任务 (使用虚拟线程 VT) 场景：调用第三方接口、查询数据库、读取文件
     */
    @Override
    @VtAsync
    public CompletableFuture<String> fetchRemoteConfig(String configKey) {
        logger.info("开始获取远程配置, Key: {}", configKey);

        try {
            // 模拟网络延迟 1 秒
            // 在 VT 模式下，这种阻塞不会占用物理线程资源
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String result = "ConfigValue_for_" + configKey;
        logger.info("远程配置获取成功: {}", result);

        return CompletableFuture.completedFuture(result);
    }

    /**
     * 🧱 模拟 CPU 密集型任务 (使用平台线程 PT) 场景：数据计算、图片压缩、复杂逻辑处理
     */
    @Override
    @PtAsync
    public void performHeavyCalculation(int seed) {
        logger.info("开始进行重度计算, Seed: {}", seed);

        long start = System.currentTimeMillis();
        // 模拟 CPU 耗时操作：循环计算哈希或大数运算
        long sum = 0;
        for (int i = 0; i < 100_000_000; i++) {
            sum += (long)i * seed;
        }

        long duration = System.currentTimeMillis() - start;
        logger.info("重度计算完成, 耗时: {}ms, 结果摘要: {}", duration, sum);
    }

    /**
     * ⚠️ 模拟一定会报错的任务 用于演示 AsyncUncaughtExceptionHandler 的追踪能力
     */
    @Override
    @VtAsync
    public void triggerErrorTask() {
        logger.info("这个任务即将抛出异常...");
        throw new RuntimeException("这是模拟的异步业务异常");
    }
}
