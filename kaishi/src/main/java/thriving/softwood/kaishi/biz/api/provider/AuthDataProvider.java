// File: biz/service/AuthDataProviderImpl.java
package thriving.softwood.kaishi.biz.api.provider;

import static thriving.softwood.common.auth.config.AncestorAuthConfig.AUTH_CACHE_MANAGER;
import static thriving.softwood.common.auth.config.AncestorAuthConfig.USER_AUTH_INFO_CACHE;
import static thriving.softwood.common.auth.constant.BaseConst.MAX_DEPT_ID;
import static thriving.softwood.common.auth.constant.BaseConst.SUPER_ADMIN_ROLE;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import cn.hutool.v7.core.util.ObjUtil;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.*;
import thriving.softwood.common.auth.infrastructure.db.master.repo.*;
import thriving.softwood.common.security.pojo.dto.DataRuleDTO;
import thriving.softwood.common.security.spi.UserAuthProvider;
import thriving.softwood.kaishi.biz.pojo.dto.UserAuthInfo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.DepartmentAssociationInfoRepo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.EmployeeAssociationInfoRepo;

/**
 * 🚀 核心大脑：实现 common-security 的数据提供者接口
 */
@Service
public class AuthDataProvider implements UserAuthProvider {

    private final CacheManager cacheManager;
    private final SysUserRepo userRepo;
    private final SysRoleRepo roleRepo;
    private final SysUserRoleRepo userRoleRepo;
    private final SysPermissionRepo permRepo;
    private final SysRolePermissionRepo rolePermRepo;
    private final SysDataRuleRepo dataRuleRepo;
    private final SysRoleDataRuleRepo roleDataRuleRepo;
    private final SysDeptRepo sysDeptRepo;
    private final EmployeeAssociationInfoRepo empAssocRepo;
    private final DepartmentAssociationInfoRepo deptAssocRepo;

    public AuthDataProvider(@Qualifier(AUTH_CACHE_MANAGER) CacheManager cacheManager, SysUserRepo userRepo,
        SysRoleRepo roleRepo, SysUserRoleRepo userRoleRepo, SysPermissionRepo permRepo,
        SysRolePermissionRepo rolePermRepo, SysDataRuleRepo dataRuleRepo, SysRoleDataRuleRepo roleDataRuleRepo,
        SysDeptRepo sysDeptRepo, EmployeeAssociationInfoRepo empAssocRepo,
        DepartmentAssociationInfoRepo deptAssocRepo) {
        this.cacheManager = cacheManager;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.permRepo = permRepo;
        this.rolePermRepo = rolePermRepo;
        this.dataRuleRepo = dataRuleRepo;
        this.roleDataRuleRepo = roleDataRuleRepo;
        this.sysDeptRepo = sysDeptRepo;
        this.empAssocRepo = empAssocRepo;
        this.deptAssocRepo = deptAssocRepo;
    }

    @Override
    public UserAuthInfo getAuthInfo(Long userId) {
        Cache cache = cacheManager.getCache(USER_AUTH_INFO_CACHE);
        if (cache == null) {
            return loadFromDb(userId);
        }
        return cache.get(userId, () -> loadFromDb(userId));
    }

    private UserAuthInfo loadFromDb(Long userId) {
        SysUser user = userRepo.getById(userId);
        if (user == null) {
            return null;
        }

        UserAuthInfo dto = new UserAuthInfo();
        dto.setId(user.getId());
        dto.setLoginAccount(user.getLoginAccount());
        dto.setDeptId(user.getDeptId());
        dto.setStatus(user.getStatus());
        dto.setPermissionVersion(user.getPermissionVersion());

        dto.setEmployeeTypeId(empAssocRepo.getTypeIdByLoginAccount(user.getLoginAccount()));
        dto.setDepartmentTypeId((ObjUtil.isEmpty(user.getDeptId()) || MAX_DEPT_ID.equals(user.getDeptId())) ? null
            : deptAssocRepo.getTypeIdByDeptId(user.getDeptId()));

        // 判定部门活跃状态
        if (user.getDeptId() != null) {
            SysDept dept = sysDeptRepo.getById(user.getDeptId());
            dto.setDepartmentActive(dept != null && dept.getStatus() == 1);
        } else {
            dto.setDepartmentActive(true);
        }

        // 获取角色
        List<Long> roleIds = userRoleRepo.listRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return emptyDto(dto);
        }

        List<SysRole> activeRoles = roleRepo.listActiveRoleByIds(roleIds);
        if (activeRoles.isEmpty()) {
            return emptyDto(dto);
        }

        List<Long> activeRoleIds = activeRoles.stream().map(SysRole::getId).toList();
        dto.setRoleCodes(activeRoles.stream().map(SysRole::getRoleCode).collect(Collectors.toSet()));

        // 🌟 上帝模式判定：基于角色编码
        dto.setGodMode(dto.getRoleCodes().contains(SUPER_ADMIN_ROLE));

        // 部门禁用拦截
        if (!dto.getGodMode() && !dto.getDepartmentActive()) {
            return emptyDto(dto);
        }

        // RBAC: 加载权限
        List<Long> permIds = rolePermRepo.listPermissionIdsByRoleIds(activeRoleIds);
        Set<String> perms = permIds.isEmpty() ? new HashSet<>()
            : permRepo.listByIds(permIds).stream().map(SysPermission::getPermissionCode)
                .filter(c -> c != null && !c.trim().isEmpty()).collect(Collectors.toSet());
        dto.setPermissions(perms);

        // ABAC: 加载数据规则 (🌟 转化为纯净的 DataRuleDTO)
        List<Long> ruleIds = roleDataRuleRepo.listDataRuleIdsByRoleIds(activeRoleIds);
        Map<String, List<DataRuleDTO>> rulesMap = new HashMap<>();
        if (!ruleIds.isEmpty()) {
            List<SysDataRule> dbRules = dataRuleRepo.listByIds(ruleIds);
            rulesMap = dbRules.stream().map(r -> {
                DataRuleDTO dr = new DataRuleDTO();
                dr.setId(r.getId());
                dr.setTargetResource(r.getTargetResource());
                dr.setScopeType(r.getScopeType());
                dr.setCustomSqlJson(r.getCustomSqlJson());
                return dr;
            }).collect(Collectors.groupingBy(DataRuleDTO::getTargetResource));
        }
        dto.setDataRules(rulesMap);

        return dto;
    }

    private UserAuthInfo emptyDto(UserAuthInfo dto) {
        dto.setRoleCodes(Collections.emptySet());
        dto.setPermissions(Collections.emptySet());
        dto.setDataRules(Collections.emptyMap());
        return dto;
    }
}