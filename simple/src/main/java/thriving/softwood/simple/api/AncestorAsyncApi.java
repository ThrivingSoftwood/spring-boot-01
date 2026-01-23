package thriving.softwood.simple.api;

import java.util.concurrent.CompletableFuture;

/**
 * 多线程示例
 * 
 * @author ThrivingSoftwood
 */
public interface AncestorAsyncApi {

    /**
     * 🚀 模拟 I/O 密集型任务 (使用虚拟线程 VT) 场景：调用第三方接口、查询数据库、读取文件
     */
    CompletableFuture<String> fetchRemoteConfig(String configKey);

    /**
     * 🧱 模拟 CPU 密集型任务 (使用平台线程 PT) 场景：数据计算、图片压缩、复杂逻辑处理
     */
    void performHeavyCalculation(int seed);

    /**
     * ⚠️ 模拟一定会报错的任务 用于演示 AsyncUncaughtExceptionHandler 的追踪能力
     */
    void triggerErrorTask();
}
