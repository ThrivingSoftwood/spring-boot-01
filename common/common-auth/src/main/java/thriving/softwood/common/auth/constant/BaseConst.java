package thriving.softwood.common.auth.constant;

public class BaseConst {
    /**
     * 部门相关
     */
    public static final String ROOT_DEPARTMENT_ID_STR = "0";
    public static final Long ROOT_PARENT_DEPT_ID_LONG = 0L;

    /**
     * 超管角色代码
     */

    public static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    /**
     * 最大部门 id
     */
    public static final Long MAX_DEPT_ID = 99999L;

    /**
     * 用户管理的人员信息前缀, D: Department, E:Employee, U:User
     */
    public static final String USER_PREFIX = "U_";
    public static final String EMPLOYEE_PREFIX = "E_";
    public static final String DEPT_PREFIX = "D_";
}
