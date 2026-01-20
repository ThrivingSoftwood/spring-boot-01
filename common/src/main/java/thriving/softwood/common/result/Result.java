package thriving.softwood.common.result;

import static thriving.softwood.common.enums.RespCodeEnum.OK;

import java.io.Serializable;

import lombok.Data;
import thriving.softwood.common.enums.RespCodeEnum;

/**
 * 统一 API 响应结果封装
 * 
 * @author ThrivingSoftwood
 * @param <T> 数据载荷的类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;

    /** 状态码 */
    private Integer code;

    /** 提示信息 (例如: "操作成功", "参数错误") */
    private String msg;

    /** 实际数据 (泛型) */
    private T data;

    /** 时间戳 (可选，用于排查日志) */
    private long timestamp = System.currentTimeMillis();

    // 私有构造，强制使用静态工厂方法
    private Result() {}

    /**
     * 成功响应 - 带数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        // 建议后续用枚举替换硬编码
        result.setSuccess(true);
        result.setCode(OK.code());
        result.setMsg(OK.enDesc());
        result.setData(data);
        return result;
    }

    /**
     * 成功响应 - 无数据
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(Integer code, String msg, Throwable e) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg + "\n" + e.getLocalizedMessage());
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(RespCodeEnum enumObj, Throwable e) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(enumObj.code());
        result.setMsg(enumObj.cnDesc() + "\n" + (null == e ? "" : ("\n" + e.getLocalizedMessage())));
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(RespCodeEnum enumObj, Throwable e, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(enumObj.code());
        result.setMsg(enumObj.cnDesc() + "\n" + (null == e ? "" : ("\n" + e.getLocalizedMessage())));
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(RespCodeEnum enumObj, String msg, Throwable e, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(enumObj.code());
        result.setMsg(enumObj.cnDesc() + "\n" + msg + (null == e ? "" : ("\n" + e.getLocalizedMessage())));
        result.setData(data);
        return result;
    }
}