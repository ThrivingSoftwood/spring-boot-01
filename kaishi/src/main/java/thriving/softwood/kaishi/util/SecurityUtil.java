// File: ./common/common-core/src/main/java/thriving/softwood/common/core/util/SecurityUtil.java
package thriving.softwood.kaishi.util;

import java.util.function.Supplier;

import thriving.softwood.kaishi.context.UserContext;

public class SecurityUtil {

    /**
     * 以系统身份执行逻辑（绕过数据权限）
     */
    public static <T> T runAsSystem(Supplier<T> supplier) {
        try {
            UserContext.setSystemMode(true);
            return supplier.get();
        } finally {
            UserContext.setSystemMode(false);
        }
    }

    /**
     * 以系统身份执行逻辑（无返回值）
     */
    public static void runAsSystem(Runnable runnable) {
        try {
            UserContext.setSystemMode(true);
            runnable.run();
        } finally {
            UserContext.setSystemMode(false);
        }
    }
}