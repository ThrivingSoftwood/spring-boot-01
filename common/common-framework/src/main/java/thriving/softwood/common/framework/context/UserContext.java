package thriving.softwood.common.framework.context;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> ACCOUNT_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(Long userId, String loginAccount) {
        USER_ID_THREAD_LOCAL.set(userId);
        ACCOUNT_THREAD_LOCAL.set(loginAccount);
    }

    public static Long getUserId() {
        return USER_ID_THREAD_LOCAL.get();
    }

    public static String getLoginAccount() {
        return ACCOUNT_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_ID_THREAD_LOCAL.remove();
        ACCOUNT_THREAD_LOCAL.remove();
    }
}