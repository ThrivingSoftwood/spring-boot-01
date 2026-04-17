package thriving.softwood.kaishi.biz.api.system;

import java.util.List;

import thriving.softwood.kaishi.biz.pojo.vo.SysPermissionVO;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysPermission;

public interface PermissionApi {

    /**
     * 📘 核心方法：获取整棵权限树
     */
    List<SysPermissionVO> treeAllPermissions();

    List<SysPermissionVO> treeMenuPermissions(String loginAccount);

    /**
     * 删除节点 (防呆设计：有子节点不允许删除)
     */
    void deletePermission(Long id);

    void add(SysPermission sysPermission);

    void update(SysPermission sysPermission);

}
