package thriving.softwood.sample.biz.api;

import java.util.concurrent.CompletableFuture;

import thriving.softwood.sample.biz.pojo.vo.AncestorVO;
import thriving.softwood.sample.biz.pojo.vo.ComplexTraceVO;

/**
 * 多线程示例
 * 
 * @author ThrivingSoftwood
 * @since version 2026-01-23
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

    /**
     * 启动多层级异步调用链
     *
     * @param requestPayload 请求参数
     * @return 包含 TraceID 的结果
     */
    AncestorVO startChain(String requestPayload);

    /**
     * 模拟 CPU 密集型任务 (平台线程)
     */
    CompletableFuture<String> processCpuTask(String payload);

    /**
     * 模拟 IO 密集型任务 (虚拟线程)
     */
    CompletableFuture<String> processIoTask(String payload);

    // ... (保留 startChain, processCpuTask, processIoTask)

    /**
     * 启动一个复杂的多层、并行异步调用链，用于压力测试链路追踪
     * 
     * @return 聚合后的结果
     */
    ComplexTraceVO startComplexChain();

    /**
     * 并行的平台线程任务
     * 
     * @param taskNum 任务编号
     * @return 任务结果
     */
    CompletableFuture<String> parallelCpuTask(int taskNum);

    /**
     * 并行的虚拟线程任务
     * 
     * @param taskNum 任务编号
     * @return 任务结果
     */
    CompletableFuture<String> parallelIoTask(int taskNum);

    /**
     * 嵌套的虚拟线程任务
     * 
     * @param parentPayload 来自父任务的数据
     * @return 任务结果
     */
    CompletableFuture<String> nestedIoTask(String parentPayload);
}
