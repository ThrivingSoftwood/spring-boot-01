// File: biz/service/AuthDataProviderImpl.java
package thriving.softwood.common.auth.api.provider;

import static thriving.softwood.common.auth.config.AncestorAuthConfig.AUTH_CACHE_MANAGER;
import static thriving.softwood.common.auth.config.AncestorAuthConfig.USER_AUTH_INFO_CACHE;
import static thriving.softwood.common.auth.constant.BaseConst.MAX_DEPT_ID;
import static thriving.softwood.common.auth.constant.BaseConst.SUPER_ADMIN_ROLE;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import cn.hutool.v7.core.util.ObjUtil;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.*;
import thriving.softwood.common.auth.infrastructure.db.master.repo.*;
import thriving.softwood.common.auth.spi.AuthBusinessProvider;
import thriving.softwood.common.security.api.provider.UserAuthProviderApi;
import thriving.softwood.common.security.pojo.dto.DataRuleDTO;
import thriving.softwood.common.security.pojo.dto.UserAuthInfoDTO;

/**
 * 🚀 核心大脑：实现 common-security 的数据提供者接口
 */
@Service
public class AuthDataProvider implements UserAuthProviderApi {

    private final CacheManager cacheManager;
    private final SysUserRepo userRepo;
    private final SysRoleRepo roleRepo;
    private final SysUserRoleRepo userRoleRepo;
    private final SysPermissionRepo permRepo;
    private final SysRolePermissionRepo rolePermRepo;
    private final SysDataRuleRepo dataRuleRepo;
    private final SysRoleDataRuleRepo roleDataRuleRepo;
    private final SysDeptRepo sysDeptRepo;
    // 🌟 注入业务回调 SPI
    private final AuthBusinessProvider businessProvider;

    public AuthDataProvider(@Qualifier(AUTH_CACHE_MANAGER) CacheManager cacheManager, SysUserRepo userRepo,
        SysRoleRepo roleRepo, SysUserRoleRepo userRoleRepo, SysPermissionRepo permRepo,
        SysRolePermissionRepo rolePermRepo, SysDataRuleRepo dataRuleRepo, SysRoleDataRuleRepo roleDataRuleRepo,
        SysDeptRepo sysDeptRepo, ObjectProvider<AuthBusinessProvider> businessProvider) {
        this.cacheManager = cacheManager;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.permRepo = permRepo;
        this.rolePermRepo = rolePermRepo;
        this.dataRuleRepo = dataRuleRepo;
        this.roleDataRuleRepo = roleDataRuleRepo;
        this.sysDeptRepo = sysDeptRepo;
        this.businessProvider = businessProvider.getIfAvailable();
    }

    @Override
    public UserAuthInfoDTO getAuthInfo(Long userId) {
        Cache cache = cacheManager.getCache(USER_AUTH_INFO_CACHE);
        if (cache == null) {
            return loadFromDb(userId);
        }
        return cache.get(userId, () -> loadFromDb(userId));
    }

    private UserAuthInfoDTO loadFromDb(Long userId) {
        SysUser user = userRepo.getById(userId);
        if (user == null) {
            return null;
        }

        UserAuthInfoDTO dto = new UserAuthInfoDTO();
        dto.setId(user.getId());
        dto.setLoginAccount(user.getLoginAccount());
        dto.setDeptId(user.getDeptId());
        dto.setStatus(user.getStatus());
        dto.setPermissionVersion(user.getPermissionVersion());

        // 🌟 通过 SPI 获取业务专属 ID，彻底解耦 ERP 映射表
        if (businessProvider != null) {
            dto.setEmployeeTypeId(businessProvider.getEmployeeTypeId(user.getLoginAccount()));
            dto.setDepartmentTypeId((ObjUtil.isEmpty(user.getDeptId()) || MAX_DEPT_ID.equals(user.getDeptId())) ? null
                : businessProvider.getDepartmentTypeId(user.getDeptId()));
        }

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

    private UserAuthInfoDTO emptyDto(UserAuthInfoDTO dto) {
        dto.setRoleCodes(Collections.emptySet());
        dto.setPermissions(Collections.emptySet());
        dto.setDataRules(Collections.emptyMap());
        return dto;
    }
}