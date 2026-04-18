package thriving.softwood.common.framework.context;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> ACCOUNT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> GOD_MODE_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(Long userId, String loginAccount, Boolean godMode) {
        USER_ID_THREAD_LOCAL.set(userId);
        ACCOUNT_THREAD_LOCAL.set(loginAccount);
        GOD_MODE_THREAD_LOCAL.set(godMode);
    }

    public static Long userId() {
        return USER_ID_THREAD_LOCAL.get();
    }

    public static String loginAccount() {
        return ACCOUNT_THREAD_LOCAL.get();
    }

    public static Boolean godMode() {
        return GOD_MODE_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_ID_THREAD_LOCAL.remove();
        ACCOUNT_THREAD_LOCAL.remove();
        GOD_MODE_THREAD_LOCAL.remove();
    }
}