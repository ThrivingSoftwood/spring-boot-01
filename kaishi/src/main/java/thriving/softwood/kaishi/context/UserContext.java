package thriving.softwood.kaishi.context;

import java.util.List;
import java.util.Map;
import java.util.Set;

import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDataRule;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> ACCOUNT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> GOD_MODE_THREAD_LOCAL = new ThreadLocal<>();

    // 🌟 新增：行级数据权限所需
    private static final ThreadLocal<Long> DEPT_ID_THREAD_LOCAL = new ThreadLocal<>();
    // 🌟 新增：列级字段与操作权限所需 (如: "purchase:price:view")
    private static final ThreadLocal<Set<String>> PERMISSIONS_THREAD_LOCAL = new ThreadLocal<>();
    // 🌟 新增：ABAC 数据规则引擎所需 (Key: targetResource 如 "DlyBuy", Value: 适用的规则列表)
    private static final ThreadLocal<Map<String, List<SysDataRule>>> DATA_RULES_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(Long userId, String loginAccount, Boolean godMode, Long deptId, Set<String> permissions,
        Map<String, List<SysDataRule>> dataRules) {
        USER_ID_THREAD_LOCAL.set(userId);
        ACCOUNT_THREAD_LOCAL.set(loginAccount);
        GOD_MODE_THREAD_LOCAL.set(godMode);
        DEPT_ID_THREAD_LOCAL.set(deptId);
        PERMISSIONS_THREAD_LOCAL.set(permissions);
        DATA_RULES_THREAD_LOCAL.set(dataRules);
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

    public static Long deptId() {
        return DEPT_ID_THREAD_LOCAL.get();
    }

    public static Set<String> permissions() {
        return PERMISSIONS_THREAD_LOCAL.get();
    }

    /**
     * 获取指定业务表的拦截规则
     */
    public static List<SysDataRule> dataRules(String targetResource) {
        Map<String, List<SysDataRule>> map = DATA_RULES_THREAD_LOCAL.get();
        return map != null ? map.get(targetResource) : null;
    }

    public static void clear() {
        USER_ID_THREAD_LOCAL.remove();
        ACCOUNT_THREAD_LOCAL.remove();
        GOD_MODE_THREAD_LOCAL.remove();
        DEPT_ID_THREAD_LOCAL.remove();
        PERMISSIONS_THREAD_LOCAL.remove();
        DATA_RULES_THREAD_LOCAL.remove();
    }
}