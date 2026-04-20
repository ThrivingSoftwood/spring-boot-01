package thriving.softwood.kaishi.context;

import java.util.List;
import java.util.Map;
import java.util.Set;

import thriving.softwood.kaishi.biz.pojo.dto.UserAuthInfoDTO;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDataRule;

public class UserContext {

    private static final ThreadLocal<Boolean> SYSTEM_MODE = ThreadLocal.withInitial(() -> false);

    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> ACCOUNT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> GOD_MODE_THREAD_LOCAL = new ThreadLocal<>();

    // 🌟 新增：行级数据权限所需
    private static final ThreadLocal<Long> DEPT_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> EMPLOYEE_TYPEID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> DEPARTMENT_TYPEID_THREAD_LOCAL = new ThreadLocal<>();
    // 🌟 新增：列级字段与操作权限所需 (如: "purchase:price:view")
    private static final ThreadLocal<Set<String>> PERMISSIONS_THREAD_LOCAL = new ThreadLocal<>();
    // 🌟 新增：ABAC 数据规则引擎所需 (Key: targetResource 如 "DlyBuy", Value: 适用的规则列表)
    private static final ThreadLocal<Map<String, List<SysDataRule>>> DATA_RULES_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(UserAuthInfoDTO authInfo) {
        USER_ID_THREAD_LOCAL.set(authInfo.getId());
        ACCOUNT_THREAD_LOCAL.set(authInfo.getLoginAccount());
        GOD_MODE_THREAD_LOCAL.set(authInfo.getGodMode());
        DEPT_ID_THREAD_LOCAL.set(authInfo.getDeptId());
        PERMISSIONS_THREAD_LOCAL.set(authInfo.getPermissions());
        DATA_RULES_THREAD_LOCAL.set(authInfo.getDataRules());
        EMPLOYEE_TYPEID_THREAD_LOCAL.set(authInfo.getEmployeeTypeId());
        DEPARTMENT_TYPEID_THREAD_LOCAL.set(authInfo.getDepartmentTypeId());
    }

    /**
     * 开启/关闭系统内部模式
     */
    public static void setSystemMode(boolean isSystem) {
        SYSTEM_MODE.set(isSystem);
    }

    public static boolean underSystemMode() {
        return SYSTEM_MODE.get();
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

    public static String employeeTypeId() {
        return EMPLOYEE_TYPEID_THREAD_LOCAL.get();
    }

    public static String departmentTypeId() {
        return DEPARTMENT_TYPEID_THREAD_LOCAL.get();
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
        EMPLOYEE_TYPEID_THREAD_LOCAL.remove();
        DEPARTMENT_TYPEID_THREAD_LOCAL.remove();
        SYSTEM_MODE.remove();
    }
}