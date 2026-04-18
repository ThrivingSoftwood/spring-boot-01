package thriving.softwood.kaishi.biz.api.system;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thriving.softwood.kaishi.biz.pojo.vo.SysPermissionVO;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysPermission;
import thriving.softwood.kaishi.infrastructure.db.master.repo.*;

@Service
public class PermissionSvc implements PermissionApi {

    private final SysPermissionRepo sysPermissionRepo;
    private final SysUserRepo sysUserRepo;
    private final SysRoleRepo sysRoleRepo;
    private final SysUserRoleRepo sysUserRoleRepo;
    private final SysRolePermissionRepo sysRolePermissionRepo;

    @Autowired
    public PermissionSvc(SysPermissionRepo sysPermissionRepo, SysUserRepo sysUserRepo, SysRoleRepo sysRoleRepo,
        SysUserRoleRepo sysUserRoleRepo, SysRolePermissionRepo sysRolePermissionRepo) {
        this.sysPermissionRepo = sysPermissionRepo;
        this.sysUserRepo = sysUserRepo;
        this.sysRoleRepo = sysRoleRepo;
        this.sysUserRoleRepo = sysUserRoleRepo;
        this.sysRolePermissionRepo = sysRolePermissionRepo;
    }

    /**
     * 📘 核心方法：获取整棵权限树
     */
    @Override
    public List<SysPermissionVO> treeAllPermissions() {
        // 1. 查询所有未被逻辑删除的权限，并按 sort_order 升序排序
        List<SysPermission> allList = sysPermissionRepo.listSorted();

        // 5. 过滤出顶级节点 (parentId == 0)，它们内部已经包含了所有子孙节点
        return getTreedVOs(allList).stream().filter(node -> node.getParentId() == 0L).collect(Collectors.toList());
    }

    @Override
    public List<SysPermissionVO> treeMenuPermissions(String loginAccount) {
        List<SysPermission> permissions = sysPermissionRepo.listMenuPermissionsByAccount(loginAccount);

        // 5. 过滤出顶级节点 (parentId == 0)，它们内部已经包含了所有子孙节点
        return getTreedVOs(permissions).stream().filter(node -> node.getParentId() == 0L).collect(Collectors.toList());
    }

    private static @NonNull List<SysPermissionVO> getTreedVOs(List<SysPermission> permissions) {
        // 2. 将 Entity 转换为 VO
        List<SysPermissionVO> allVOs = permissions.stream().map(SysPermissionVO::new).toList();

        // 3. 按照 parentId 分组 (极其高效的 Java Stream API)
        Map<Long, List<SysPermissionVO>> childrenMap =
            allVOs.stream().collect(Collectors.groupingBy(SysPermissionVO::getParentId));

        // 4. 遍历所有节点，将子节点塞入对应的父节点中
        allVOs.forEach(node -> {
            List<SysPermissionVO> children = childrenMap.get(node.getId());
            if (children != null) {
                node.setChildren(children);
            }
        });
        return allVOs;
    }

    /**
     * 删除节点 (防呆设计：有子节点不允许删除)
     */
    @Override
    public void deletePermission(Long id) {
        long childCount = sysPermissionRepo.countSubNodes(id);
        if (childCount > 0) {
            throw new RuntimeException("该节点下包含子节点，禁止直接删除！");
        }
        sysPermissionRepo.logicDeleteById(id);
    }

    // (新增和修改的方法直接调用 permissionRepo.save / updateById 即可，略)
    @Override
    public void add(SysPermission sysPermission) {
        sysPermissionRepo.save(sysPermission);
    }

    @Override
    public void update(SysPermission sysPermission) {
        sysPermissionRepo.updateById(sysPermission);
    }
}