package thriving.softwood.kaishi.biz.constant;

public class BaseConst {
    /**
     * 凯诗采购单到货状态名
     */
    public static final String STATUS_PENDING_ARRIVAL = "未到货";
    public static final String STATUS_PARTIAL_RECEIPT = "部分到货";
    public static final String STATUS_OVER_RECEIPT = "超量到货";
    public static final String STATUS_FULLY_RECEIVED = "已完成";

    /**
     * 凯诗采购单追踪报表颜色控制
     */
    public static final String STATUS_TAG_SUCCESS = "success";
    public static final String STATUS_TAG_DANGER = "danger";
    public static final String STATUS_TAG_WARNING = "warning";

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
     * 每个用户的默认密码
     */
    public static final String DEFAULT_USER_PASSWORD = "123456";

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
