package thriving.softwood.common.web.component.exception.handler;

import static thriving.softwood.common.core.enums.RespCodeEnum.FORBIDDEN;
import static thriving.softwood.common.core.enums.RespCodeEnum.INTERNAL_SERVER_ERROR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.servlet.http.HttpServletRequest;
import thriving.softwood.common.core.enums.RespCodeEnum;
import thriving.softwood.common.core.event.SystemErrorEvent;
import thriving.softwood.common.core.exception.AuthException;
import thriving.softwood.common.core.exception.DetailException;
import thriving.softwood.common.core.exception.TokenException;
import thriving.softwood.common.core.result.Result;
import thriving.softwood.common.web.spi.UserContextProvider;

/**
 * 全局统一异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ApplicationEventPublisher eventPublisher;
    private final UserContextProvider userContextProvider;

    public GlobalExceptionHandler(ApplicationEventPublisher eventPublisher, UserContextProvider userContextProvider) {
        this.eventPublisher = eventPublisher;
        this.userContextProvider = userContextProvider;
    }

    /**
     * 专门捕获 TokenException，将其转换为 HTTP 401 返回
     */
    @ExceptionHandler(TokenException.class)
    @RequestMapping(produces = "application/json")
    public Result<String> handleTokenException(TokenException e, HttpServletRequest request) {
        // 返回 HTTP 状态码 401
        logger.warn("🔐 Token 拦截: URL={}, Reason={}", request.getRequestURI(), e.getMessage());
        return Result.error(RespCodeEnum.UNAUTHORIZED, e, request.getRequestURI());
    }

    /**
     * 专门捕获 TokenException，将其转换为 HTTP 401 返回
     */
    @ExceptionHandler(AuthException.class)
    public Result<String> handleLoginException(TokenException e, HttpServletRequest request) {
        logger.warn("🔐 鉴权拦截: URL={}, Reason={}", request.getRequestURI(), e.getMessage());
        return Result.error(INTERNAL_SERVER_ERROR.code(), e.getMessage());
    }

    /**
     * 专门捕获 TokenException，将其转换为 HTTP 401 返回
     */
    @ExceptionHandler(DetailException.class)
    public Result<String> handleDetailException(DetailException e, HttpServletRequest request) {
        // 返回 HTTP 状态码 403
        return Result.error(FORBIDDEN.code(), e.getMessage());
    }

    /**
     * 专项拦截：文件上传大小超出限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.warn("🛑 文件上传失败：文件大小超出系统允许的阈值");

        // 从异常信息中提取具体的限制值，给用户更精准的提示
        return Result.error(RespCodeEnum.BAD_REQ.code(), "上传失败：单个文件大小不能超过配置限额!");
    }

    /**
     * 🎧 专项拦截：客户端主动断开异步请求 (SSE 长连接断开) 这是一个正常现象，不需要打印长长的 Error 堆栈，也不需要报警。
     */
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public Result<String> handleAsyncRequestNotUsableException(AsyncRequestNotUsableException e) {
        // 仅仅打印一行 info 或 debug 级别的日志即可
        logger.info("📡 SSE 连接已关闭：客户端断开连接 (Broken pipe)");

        // 这里其实前端已经收不到响应了，因为连接已经断了。
        // 返回一个标准数据结构只是为了符合代码规范。
        return Result.error(INTERNAL_SERVER_ERROR.code(), "连接已断开");
    }

    /**
     * 2. 🌟 核心：处理所有未知的系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleGlobalException(Exception e, HttpServletRequest request) {
        // 🛡️ 1. 内部记录完整日志
        logger.error("!!! 系统内部严重异常 !!! URL: {}, 信息如下：", request.getRequestURI(), e);

        // 🌟 发送异步事件，不再关心后续谁处理
        eventPublisher
            .publishEvent(new SystemErrorEvent(userContextProvider.getUserId(), this, e, request.getRequestURI()));

        // 🛡️ 3. 对外脱敏返回
        return Result.error(INTERNAL_SERVER_ERROR.code(), "服务器开小差了，请稍后再试或联系管理员");
    }

    // 你还可以继续添加 @ExceptionHandler(Exception.class) 处理其他500兜底异常
}