package thriving.softwood.kaishi.biz.api.system;

import java.util.List;

import thriving.softwood.kaishi.biz.pojo.record.UserReq;
import thriving.softwood.kaishi.biz.pojo.vo.OrgNodeVO;

public interface UserApi {
    /** 获取本地系统的 部门-用户 树 */
    List<OrgNodeVO> treeSynced();

    /** 获取管家婆中 未同步的 部门-人员 树 */
    List<OrgNodeVO> treeUnsynced();

    /** 从管家婆引入人员并开通本地账号 */
    void sync(UserReq req);

    /** 更新本地用户基础信息 (部门、状态) */
    void updateUser(UserReq req);

    /** 重置用户密码 */
    void resetPassword(UserReq req);

    /** 移除用户 (逻辑删除及解绑) */
    void deleteUser(Long id);
}