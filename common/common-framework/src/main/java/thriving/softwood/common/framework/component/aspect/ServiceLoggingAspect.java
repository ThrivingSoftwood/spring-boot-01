package thriving.softwood.common.framework.component.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一服务日志切面 拦截所有以 Api 和 Svc 结尾的 Spring Bean
 * 
 * @author CodeOmni
 */
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    // 🔍 诊断点：如果启动没看到这行，说明包扫描没扫到这个类
    @PostConstruct
    public void init() {
        log.info("🚀 [AOP 诊断] ServiceLoggingAspect 已加载，正在监听 *Api 和 *Svc 结尾的类...");
    }

    /**
     * 🔥 核心点位表达式： 1. within(..*Api)：匹配包下任何以 Api 结尾的类 2. within(..*Svc)：匹配包下任何以 Svc 结尾的类
     */
    @Pointcut("execution(* thriving.softwood..*Api.*(..)) || execution(* thriving.softwood..*Svc.*(..))")
    public void apiOrSvcPointcut() {}

    @Around("apiOrSvcPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取类名、方法名
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // ===================== 【新增：获取并打印入参】 =====================
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        // 参数名数组
        String[] paramNames = signature.getParameterNames();
        // 参数值数组
        Object[] paramValues = joinPoint.getArgs();

        // 拼接参数日志：参数名=参数值
        StringBuilder paramsSb = new StringBuilder();
        if (paramNames != null && paramValues != null) {
            for (int i = 0; i < paramNames.length; i++) {
                paramsSb.append(paramNames[i]).append("=").append(paramValues[i]);
                if (i < paramNames.length - 1) {
                    paramsSb.append(", ");
                }
            }
        }

        // 无参数时显示
        String paramsStr = paramsSb.length() > 0 ? paramsSb.toString() : "无入参";
        // =================================================================

        // 1. 执行前日志（包含入参）
        log.info("开始执行 {}#{}，入参：{}", className, methodName, paramsStr);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            // 执行目标方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 2. 异常日志
            log.error("执行 {}#{} 异常，入参：{}，错误：{}", className, methodName, paramsStr, e.getMessage(), e);
            throw e;
        }

        long duration = System.currentTimeMillis() - startTime;

        // 耗时警告
        if (duration > 1000) {
            log.warn("⚠️ {}#{} 耗时较长：{} ms", className, methodName, duration);
        }

        // 3. 执行结束日志（可选择打印返回值）
        log.info("结束执行 {}#{}，耗时：{} ms，返回：{}", className, methodName, duration, result == null ? "void/null" : result);

        return result;
    }
}