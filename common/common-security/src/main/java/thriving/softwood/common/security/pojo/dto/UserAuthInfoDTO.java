package thriving.softwood.common.security.pojo.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

/**
 * 用户全链路权限上下文 DTO
 */
@Data
public class UserAuthInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String loginAccount;
    protected Long deptId;
    protected Byte status;
    protected String permissionVersion;
    // 🌟 新增：部门活跃状态
    protected Boolean departmentActive;
    protected Boolean godMode = false;

    // RBAC: 当前用户拥有的所有角色编码
    protected Set<String> roleCodes;
    // RBAC: 当前用户拥有的所有操作权限和字段权限 (如 purchase:price:view)
    protected Set<String> permissions;
    // 业务模块特有的关联标识
    protected String employeeTypeId;
    protected String departmentTypeId;

    // ABAC: 当前用户受控的数据范围规则 (按 TargetResource 分组)
    // 🌟 修改：使用 DataRuleDTO 替换原来的 SysDataRule
    protected Map<String, List<DataRuleDTO>> dataRules;
}