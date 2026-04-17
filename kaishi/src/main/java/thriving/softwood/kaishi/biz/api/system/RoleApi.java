// RoleApi.java
package thriving.softwood.kaishi.biz.api.system;

import java.util.List;

import thriving.softwood.kaishi.biz.pojo.record.RoleReq;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysRole;

public interface RoleApi {
    List<SysRole> listRoles();

    void saveOrUpdateRole(RoleReq req);

    void deleteRole(Long id);

    // 获取分配信息
    List<Long> getAssignedUserIds(Long roleId);

    List<Long> getAssignedPermissionIds(Long roleId);

    List<Long> getAssignedDataRuleIds(Long roleId);

    // 执行分配 (核心)
    void assignUsers(RoleReq req);

    void assignPermissions(RoleReq req);

    void assignDataRules(RoleReq req);
}