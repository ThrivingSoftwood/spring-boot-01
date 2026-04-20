// File: context/UserContext.java
package thriving.softwood.common.security.context;

import java.util.List;
import java.util.Map;
import java.util.Set;
import thriving.softwood.common.security.pojo.dto.DataRuleDTO;
import thriving.softwood.common.security.pojo.dto.UserAuthInfoDTO;

public class UserContext {
    private static final ThreadLocal<Boolean> SYSTEM_MODE = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> ACCOUNT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> GOD_MODE_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Long> DEPT_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> EMPLOYEE_TYPEID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> DEPARTMENT_TYPEID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Set<String>> PERMISSIONS_THREAD_LOCAL = new ThreadLocal<>();
    // 🌟 修改泛型为 DataRuleDTO
    private static final ThreadLocal<Map<String, List<DataRuleDTO>>> DATA_RULES_THREAD_LOCAL = new ThreadLocal<>();

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

    // 🌟 修改返回类型为 DataRuleDTO
    public static List<DataRuleDTO> dataRules(String targetResource) {
        Map<String, List<DataRuleDTO>> map = DATA_RULES_THREAD_LOCAL.get();
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