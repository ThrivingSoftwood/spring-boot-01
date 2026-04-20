package thriving.softwood.common.auth.api.system;

import java.util.List;

import thriving.softwood.common.auth.pojo.record.UserReq;
import thriving.softwood.common.auth.pojo.vo.OrganizationNodeVO;

public interface UserApi {
    /** 获取本地系统的 部门-用户 树 */
    List<OrganizationNodeVO> treeSynced();

    /** 更新本地用户基础信息 (部门、状态) */
    void updateUser(UserReq req);

    /** 重置用户密码 */
    void resetPassword(UserReq req);

    /** 移除用户 (逻辑删除及解绑) */
    void deleteUser(Long id);

    List<Long> listAssignedRoleIdsByUserId(Long userId);

    void assignRoles(Long userId, List<Long> roleIds);
}