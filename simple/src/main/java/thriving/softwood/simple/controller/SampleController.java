package thriving.softwood.simple.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import thriving.softwood.common.core.result.Result;
import thriving.softwood.simple.api.AncestorAsyncApi;
import thriving.softwood.simple.component.config.Sample;
import thriving.softwood.simple.pojo.vo.AncestorVO;
import thriving.softwood.simple.pojo.vo.ComplexTraceVO;

/**
 * @author ThrivingSoftwood
 * @apiNote 指定当前 bean 在哪个环境下被启用@org.springframework.context.annotation.Profile(value = "dev")
 */

@RestController
@RequestMapping("/sample")
public class SampleController {
    private final Logger logger = LoggerFactory.getLogger(SampleController.class);

    // 1. 配置文件内容注入属性,通过注解 @Value
    @Value(value = "${sample.id}")
    Integer sampleId;
    @Value(value = "${sample.name}")
    String sampleName;
    // 2. 通过注解 @ConfigurationProperties 直接注入,
    // 类名必须与对应层级的配置项名一致
    @Resource
    Sample sample;

    // 多线程注解使用示例
    @Resource
    AncestorAsyncApi ancestorAsyncApi;

    @RequestMapping("/sample01")
    public String sample01() {
        logger.info("实际日志记录对象类型:{}", logger.getClass().getName());
        logger.info("sampleId:{},sampleName:{}", sampleId, sampleName);
        logger.info("sample 配置:{}", sample);
        logger.trace("测试 trace");
        return "Hello World!";
    }

    @RequestMapping("/async")
    public Result<String> testAsync() {
        logger.info("=== 接收到前端请求，开始演示异步体系 ===");

        // 1. 调用 VT 异步任务 (带返回值)
        ancestorAsyncApi.fetchRemoteConfig("order-timeout-setting")
            .thenAccept(val -> logger.info("回调处理: 最终拿到的配置是 {}", val));

        // 2. 调用 PT 异步任务 (无返回值)
        ancestorAsyncApi.performHeavyCalculation(88);

        // 3. 触发一个必然报错的任务
        ancestorAsyncApi.triggerErrorTask();

        logger.info("=== 主线程逻辑执行完毕，已响应前端 ===");
        return Result.success("请求已受理，请查看后台控制台日志。");
    }

    /**
     * 触发多层级线程调用链 GET /simple/chain?msg=hello
     */
    @RequestMapping("/chain")
    public AncestorVO triggerChain(@RequestParam(defaultValue = "hello") String msg) {
        return ancestorAsyncApi.startChain(msg);
    }

    // ... (保留 /chain 接口)

    /**
     * 触发复杂的多层级并行调用链 GET /simple/complex-chain
     */
    @RequestMapping("/complex-chain")
    public ComplexTraceVO triggerComplexChain() {
        return ancestorAsyncApi.startComplexChain();
    }
}
