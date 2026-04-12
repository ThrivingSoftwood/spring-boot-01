package thriving.softwood.common.core.exception;

import java.io.Serial;

/**
 * 专属认证异常：Token 失效、未登录、被篡改时抛出
 */
public class TokenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public TokenException(String message) {
        super(message);
    }
}