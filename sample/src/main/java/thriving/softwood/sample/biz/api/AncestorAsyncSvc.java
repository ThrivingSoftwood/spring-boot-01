package thriving.softwood.sample.biz.api;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.Resource;
import thriving.softwood.common.framework.annotation.async.PtAsync;
import thriving.softwood.common.framework.annotation.async.VtAsync;
import thriving.softwood.sample.biz.pojo.vo.AncestorVO;
import thriving.softwood.sample.biz.pojo.vo.ComplexTraceVO;

/**
 * 多线程示例
 * 
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
@Service
public class AncestorAsyncSvc implements AncestorAsyncApi {

    private static final Logger logger = LoggerFactory.getLogger(AncestorAsyncSvc.class);

    @Resource
    private Tracer tracer;

    /**
     * 注入自身代理，解决类内部调用 @Async 失效的问题
     */
    @Resource
    @Lazy
    private AncestorAsyncSvc self;

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

    @Override
    public AncestorVO startChain(String requestPayload) {
        try {
            logger.info("🟢 [1. Main Thread] 收到请求, 准备派发任务. Thread: {}", Thread.currentThread().getName());

            // 1. 调用平台线程任务 (CPU 密集)
            // 注意：必须通过 self 调用，触发 AOP 拦截 -> MicrometerTracingDecorator 生效
            self.processCpuTask(requestPayload);

            String traceId =
                tracer.currentSpan() != null ? Objects.requireNonNull(tracer.currentSpan()).context().traceId() : "N/A";

            logger.info("✅ [1. Main Thread] 主线程任务分发完毕，立即返回响应.");

            return AncestorVO.builder().traceId(traceId).message("任务链已启动，请查看后台日志").timestamp(System.currentTimeMillis())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @PtAsync // 使用平台线程池
    public CompletableFuture<String> processCpuTask(String payload) {

        // 睡眠 5 秒
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            logger.info("测试而已");
        }
        logger.info("🟡 [2. Platform Thread] 开始执行 CPU 密集型计算... Thread: {}", Thread.currentThread().getName());

        try {
            // 模拟计算耗时
            TimeUnit.MILLISECONDS.sleep(50);

            // 2. 嵌套调用：触发虚拟线程任务 (IO 密集)
            logger.info("🟡 [2. Platform Thread] 计算完成，准备调用远程 IO 接口...");
            self.processIoTask(payload + "-processed");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return CompletableFuture.completedFuture("CPU Done");
    }

    @Override
    @VtAsync // 使用虚拟线程池
    public CompletableFuture<String> processIoTask(String payload) {
        logger.info("🟣 [3. Virtual Thread] 开始执行 IO 操作 (查库/RPC)... Thread: {}", Thread.currentThread().getName());

        try {
            // 模拟 IO 阻塞 (虚拟线程挂起，不阻塞载体线程)
            TimeUnit.MILLISECONDS.sleep(100);
            logger.info("🟣 [3. Virtual Thread] IO 操作完成. 数据已写入.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return CompletableFuture.completedFuture("IO Done");
    }

    // ==========================================================
    // 复杂链路追踪测试实现
    // ==========================================================

    @Override
    public ComplexTraceVO startComplexChain() {
        long startTime = System.currentTimeMillis();
        logger.info("🟢 [1. Main Thread] 收到复杂链路请求, 准备并行派发任务.");

        // --- Fan-out ---
        // 并行启动两个任务，一个用平台线程，一个用虚拟线程
        CompletableFuture<String> cpuFuture = self.parallelCpuTask(1);
        CompletableFuture<String> ioFuture = self.parallelIoTask(1);

        // --- Fan-in ---
        // 等待所有并行的根任务完成
        CompletableFuture.allOf(cpuFuture, ioFuture).join();

        String cpuResult, ioResult;
        try {
            cpuResult = cpuFuture.get();
            ioResult = ioFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            cpuResult = "Error";
            ioResult = "Error";
        }

        long duration = System.currentTimeMillis() - startTime;
        String traceId =
            tracer.currentSpan() != null ? Objects.requireNonNull(tracer.currentSpan()).context().traceId() : "N/A";

        logger.info("✅ [1. Main Thread] 所有并行任务完成，聚合结果. Duration: {}ms", duration);

        return ComplexTraceVO.builder().traceId(traceId).finalMessage("All tasks completed.").cpuTaskResult(cpuResult)
            .ioTaskResult(ioResult).durationMs(duration).build();
    }

    @Override
    @PtAsync // 使用平台线程
    public CompletableFuture<String> parallelCpuTask(int taskNum) {
        logger.info("🟡 [2. Platform Thread] 开始执行并行 CPU 任务 #{}.", taskNum);
        try {
            TimeUnit.MILLISECONDS.sleep(50); // 模拟计算

            // --- Nested Call ---
            // 在平台线程内，再启动一个嵌套的虚拟线程任务
            CompletableFuture<String> nestedFuture = self.nestedIoTask("Data from CPU task " + taskNum);
            nestedFuture.join(); // 等待嵌套任务完成

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("🟡 [2. Platform Thread] CPU 任务 #{} 及嵌套任务已完成.", taskNum);
        return CompletableFuture.completedFuture("CPU Task " + taskNum + " OK");
    }

    @Override
    @VtAsync // 使用虚拟线程
    public CompletableFuture<String> parallelIoTask(int taskNum) {
        logger.info("🟣 [3. Virtual Thread] 开始执行并行 IO 任务 #{}.", taskNum);
        try {
            TimeUnit.MILLISECONDS.sleep(100); // 模拟 IO
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("🟣 [3. Virtual Thread] IO 任务 #{} 已完成.", taskNum);
        return CompletableFuture.completedFuture("IO Task " + taskNum + " OK");
    }

    @Override
    @VtAsync // 使用虚拟线程
    public CompletableFuture<String> nestedIoTask(String parentPayload) {
        logger.info("🔵 [4. Nested Virtual Thread] 开始执行嵌套 IO 任务. Payload: {}", parentPayload);
        try {
            TimeUnit.MILLISECONDS.sleep(20); // 模拟 DB 写入
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("🔵 [4. Nested Virtual Thread] 嵌套 IO 任务完成.");
        return CompletableFuture.completedFuture("Nested IO OK");
    }
}
