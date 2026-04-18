package thriving.softwood.kaishi.biz.pojo.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDataRule;

/**
 * 用户全链路权限上下文 DTO
 */
@Data
public class UserAuthInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String loginAccount;
    private Long deptId;
    private Byte status;
    private String permissionVersion;

    // RBAC: 当前用户拥有的所有角色编码
    private Set<String> roleCodes;
    // RBAC: 当前用户拥有的所有操作权限和字段权限 (如 purchase:price:view)
    private Set<String> permissions;
    // ABAC: 当前用户受控的数据范围规则 (按 TargetResource 分组)
    private Map<String, List<SysDataRule>> dataRules;
}