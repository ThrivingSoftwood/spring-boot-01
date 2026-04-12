package thriving.softwood.common.web.component.exception.handler;

import static thriving.softwood.common.core.enums.RespCodeEnum.INTERNAL_SERVER_ERROR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import thriving.softwood.common.core.enums.RespCodeEnum;
import thriving.softwood.common.core.exception.AuthException;
import thriving.softwood.common.core.exception.TokenException;
import thriving.softwood.common.core.result.Result;

/**
 * 全局统一异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 专门捕获 TokenException，将其转换为 HTTP 401 返回
     */
    @ExceptionHandler(TokenException.class)
    public Result<String> handleTokenException(TokenException e, HttpServletRequest request) {
        // 返回 HTTP 状态码 401
        return Result.error(RespCodeEnum.UNAUTHORIZED, e, request.getRequestURI());
    }

    /**
     * 专门捕获 TokenException，将其转换为 HTTP 401 返回
     */
    @ExceptionHandler(AuthException.class)
    public Result<String> handleLoginException(TokenException e, HttpServletRequest request) {
        return Result.error(INTERNAL_SERVER_ERROR.code(), e.getMessage());
    }

    /**
     * 2. 🌟 核心：处理所有未知的系统异常 (包括数据库超时、SQL 报错等) 我们绝对不能把 Exception e 传给 Result.error()，否则堆栈信息会被序列化给前端。
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleGlobalException(Exception e) {
        // 🛡️ 内部打印：在服务器后台日志中记录完整的 SQL 报错信息，方便开发排查
        logger.error("!!! 系统内部严重异常 !!! 信息如下：", e);

        // 🛡️ 对外脱敏：只给前端返回一个模糊的、友好的提示，隐藏所有 SQL 细节
        // 注意：这里使用的是自定义字符串，而不是 e.getMessage()
        return Result.error(INTERNAL_SERVER_ERROR.code(), "服务器开小差了，请稍后再试或联系管理员");
    }
    // 你还可以继续添加 @ExceptionHandler(Exception.class) 处理其他500兜底异常
}