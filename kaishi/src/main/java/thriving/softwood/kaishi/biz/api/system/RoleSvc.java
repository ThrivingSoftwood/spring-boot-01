package thriving.softwood.kaishi.biz.api.system;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.v7.core.date.DateUtil;
import thriving.softwood.common.core.exception.DetailException;
import thriving.softwood.kaishi.biz.pojo.record.RoleReq;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysRole;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysRoleDataRule;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysRolePermission;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysUserRole;
import thriving.softwood.kaishi.infrastructure.db.master.repo.*;

@Service
public class RoleSvc implements RoleApi {

    private final SysRoleRepo roleRepo;
    private final SysUserRoleRepo userRoleRepo;
    private final SysRolePermissionRepo rolePermRepo;
    private final SysRoleDataRuleRepo roleDataRuleRepo;
    private final SysUserRepo userRepo;

    public RoleSvc(SysRoleRepo roleRepo, SysUserRoleRepo userRoleRepo, SysRolePermissionRepo rolePermRepo,
        SysRoleDataRuleRepo roleDataRuleRepo, SysUserRepo userRepo) {
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.rolePermRepo = rolePermRepo;
        this.roleDataRuleRepo = roleDataRuleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<SysRole> listRoles() {
        return roleRepo.listAll();
    }

    @Override
    public void saveOrUpdateRole(RoleReq req) {
        // 1. 获取旧数据用于对比状态
        SysRole oldRole = null;
        if (null == req.id()) {
            validateNewRoleCode(req.roleCode());
        } else {
            oldRole = roleRepo.getById(req.id());
        }

        SysRole role = new SysRole();
        role.setId(req.id());
        role.setRoleCode(req.roleCode());
        role.setRoleName(req.roleName());
        role.setSortOrder(req.sortOrder());
        role.setStatus(req.status() != null ? req.status() : 1);
        roleRepo.saveOrUpdate(role);

        if (oldRole != null && (oldRole.getStatus() == 1 && role.getStatus() == 0)) {
            // 碰撞该角色下所有用户的版本号，强制触发前端静默刷新
            bumpVersionByRoleId(role.getId());
        }
    }

    private void validateNewRoleCode(String roleCode) {
        if (roleRepo.countRoleCode(roleCode) > 0L) {
            throw new DetailException("您希望新增的用户编码 [" + roleCode + "] 已存在!");
        }
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        // 1. 逻辑删除角色
        roleRepo.logicDeleteById(id);

        // 2. 获取该角色下的所有用户，触发静默刷新
        bumpVersionByRoleId(id);

        // 3. 清理关联表 (物理删除关联数据，防止占用空间)
        userRoleRepo.logicDeleteByRoleId(id);
        rolePermRepo.logicDeleteByRoleId(id);
        roleDataRuleRepo.logicDeleteByRoleId(id);
    }

    // ================= 查询分配信息 =================
    @Override
    public List<Long> getAssignedUserIds(Long roleId) {
        return userRoleRepo.listAssignedUserIdsByRoleId(roleId);
    }

    @Override
    public List<Long> getAssignedPermissionIds(Long roleId) {
        return rolePermRepo.listAssignedPermissionIdsByRoleId(roleId);
    }

    @Override
    public List<Long> getAssignedDataRuleIds(Long roleId) {
        return roleDataRuleRepo.listAssignedDataRuleIds(roleId);
    }

    // ================= 核心：执行分配并触发静默刷新 =================
    @Override
    @Transactional
    public void assignUsers(RoleReq req) {
        // 🌟 特殊处理：分配用户时，被移除的老用户 和 刚加入的新用户 都需要被刷新！
        Set<Long> affectedUserIds = new HashSet<>(userRoleRepo.listAssignedUserIdsByRoleId(req.id()));
        if (req.userIds() != null) {
            affectedUserIds.addAll(req.userIds());
        }

        // 1. 清理旧关系
        userRoleRepo.logicDeleteByRoleId(req.id());
        // 2. 插入新关系
        if (req.userIds() != null && !req.userIds().isEmpty()) {
            List<SysUserRole> list = req.userIds().stream().map(uid -> {
                SysUserRole ur = new SysUserRole();
                ur.setRoleId(req.id());
                ur.setUserId(uid);
                return ur;
            }).collect(Collectors.toList());
            userRoleRepo.addAll(list);
        }
        // 3. 刷新受影响用户
        bumpVersionByUserIds(affectedUserIds);
    }

    @Override
    @Transactional
    public void assignPermissions(RoleReq req) {
        rolePermRepo.logicDeleteByRoleId(req.id());
        if (req.permissionIds() != null && !req.permissionIds().isEmpty()) {
            List<SysRolePermission> list = req.permissionIds().stream().map(pid -> {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(req.id());
                rp.setPermissionId(pid);
                return rp;
            }).collect(Collectors.toList());
            rolePermRepo.addAll(list);
        }
        // 角色权限变了，该角色下所有用户受影响
        bumpVersionByRoleId(req.id());
    }

    @Override
    @Transactional
    public void assignDataRules(RoleReq req) {
        roleDataRuleRepo.logicDeleteByRoleId(req.id());
        if (req.dataRuleIds() != null && !req.dataRuleIds().isEmpty()) {
            List<SysRoleDataRule> list = req.dataRuleIds().stream().map(did -> {
                SysRoleDataRule rr = new SysRoleDataRule();
                rr.setRoleId(req.id());
                rr.setRuleId(did);
                return rr;
            }).collect(Collectors.toList());
            roleDataRuleRepo.addAll(list);
        }
        bumpVersionByRoleId(req.id());
    }

    // --- 内部：版本更新触发器 ---
    private void bumpVersionByRoleId(Long roleId) {
        List<Long> uids = userRoleRepo.listAssignedUserIdsByRoleId(roleId);
        bumpVersionByUserIds(new HashSet<>(uids));
    }

    private void bumpVersionByUserIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        String newVersion = DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss");
        userRepo.updatePermissionVersionsByUserIds(newVersion, userIds.stream().toList());
    }
}