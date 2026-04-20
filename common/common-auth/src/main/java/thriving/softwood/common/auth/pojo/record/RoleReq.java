// RoleReq.java - 用于接收前端的保存、分配请求
package thriving.softwood.common.auth.pojo.record;

import java.util.List;

public record RoleReq(Long id, String roleCode, String roleName, Integer sortOrder, Byte status,
    // 分配用户时用
    List<Long> userIds,
    // 分配菜单/字段权限时用
    List<Long> permissionIds,
    // 分配数据规则时用
    List<Long> dataRuleIds) {
}