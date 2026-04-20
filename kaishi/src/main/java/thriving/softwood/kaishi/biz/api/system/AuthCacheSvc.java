package thriving.softwood.kaishi.biz.api.system;

import static thriving.softwood.kaishi.biz.constant.BaseConst.MAX_DEPT_ID;
import static thriving.softwood.kaishi.biz.constant.BaseConst.SUPER_ADMIN_ROLE;

import java.util.*;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import cn.hutool.v7.core.util.ObjUtil;
import thriving.softwood.kaishi.biz.pojo.dto.UserAuthInfoDTO;
import thriving.softwood.kaishi.infrastructure.cache.local.KaishiCaffeineCacheConfig;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.DepartmentAssociationInfoRepo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.EmployeeAssociationInfoRepo;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.*;
import thriving.softwood.kaishi.infrastructure.db.master.repo.*;

/**
 * 全链路权限缓存引擎 负责从 DB 组装复杂权限模型，并依托 Caffeine 提供 O(1) 极速读取
 */
@Service
public class AuthCacheSvc {

    private final CacheManager cacheManager;
    private final SysUserRepo userRepo;
    private final SysRoleRepo roleRepo;
    private final SysUserRoleRepo userRoleRepo;
    private final SysPermissionRepo permRepo;
    private final SysRolePermissionRepo rolePermRepo;
    private final SysDataRuleRepo dataRuleRepo;
    private final SysRoleDataRuleRepo roleDataRuleRepo;
    private final DepartmentAssociationInfoRepo deptAssocRepo;
    private final EmployeeAssociationInfoRepo employeeAssocRepo;
    private final SysDeptRepo sysDeptRepo;

    public AuthCacheSvc(CacheManager cacheManager, SysUserRepo userRepo, SysRoleRepo roleRepo,
        SysUserRoleRepo userRoleRepo, SysPermissionRepo permRepo, SysRolePermissionRepo rolePermRepo,
        SysDataRuleRepo dataRuleRepo, SysRoleDataRuleRepo roleDataRuleRepo, DepartmentAssociationInfoRepo deptAssocRepo,
        EmployeeAssociationInfoRepo employeeAssocRepo, SysDeptRepo sysDeptRepo) {
        this.cacheManager = cacheManager;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.permRepo = permRepo;
        this.rolePermRepo = rolePermRepo;
        this.dataRuleRepo = dataRuleRepo;
        this.roleDataRuleRepo = roleDataRuleRepo;
        this.deptAssocRepo = deptAssocRepo;
        this.employeeAssocRepo = employeeAssocRepo;
        this.sysDeptRepo = sysDeptRepo;
    }

    public List<UserAuthInfoDTO> listAllUserAuthInfo() {
        List<UserAuthInfoDTO> result = new ArrayList<>();
        List<SysUser> users = userRepo.listAll();
        for (SysUser user : users) {
            result.add(getUserAuthInfo(user.getId()));
        }
        return result;
    }

    /**
     * 🚀 获取用户权限上下文 (使用 CacheManager 的 Get-Or-Load 机制) JwtInterceptor 在每次请求时调用此方法，耗时极低。
     */
    public UserAuthInfoDTO getUserAuthInfo(Long userId) {
        Cache cache = cacheManager.getCache(KaishiCaffeineCacheConfig.USER_AUTH_INFO_CACHE);
        if (cache == null) {
            return loadFromDb(userId);
        }

        // 如果缓存中有，直接返回；如果没有，调用 loadFromDb 查库并自动塞入缓存
        return cache.get(userId, () -> loadFromDb(userId));
    }

    /**
     * 深入 DB 组装用户权限模型
     */
    private UserAuthInfoDTO loadFromDb(Long userId) {
        SysUser user = userRepo.getById(userId);
        if (user == null) {
            return null;
        }

        UserAuthInfoDTO dto = initAndLoadDto(user);

        // 1. 获取 Role IDs
        List<Long> roleIds = userRoleRepo.listRoleIdsByUserId(userId);

        if (roleIds.isEmpty()) {
            dto.setRoleCodes(Collections.emptySet());
            dto.setPermissions(Collections.emptySet());
            dto.setDataRules(Collections.emptyMap());
            return dto;
        }

        // 2. 查 Role Codes：从数据库查出角色详情，并且只保留 status = 1 的角色
        List<SysRole> activeRoles = roleRepo.listActiveRoleByIds(roleIds);

        if (activeRoles.isEmpty()) {
            dto.setRoleCodes(Collections.emptySet());
            dto.setPermissions(Collections.emptySet());
            dto.setDataRules(Collections.emptyMap());
            return dto;
        }

        List<Long> activeRoleIds = activeRoles.stream().map(SysRole::getId).toList();
        dto.setRoleCodes(activeRoles.stream().map(SysRole::getRoleCode).collect(Collectors.toSet()));

        // 判断是否为超管
        dto.setGodMode(dto.getRoleCodes().contains(SUPER_ADMIN_ROLE));

        // 如果部门被禁用了，直接返回空权限集，或者在拦截器里处理
        if (!dto.getGodMode() && !dto.getDepartmentActive()) {
            dto.setRoleCodes(Collections.emptySet());
            dto.setPermissions(Collections.emptySet());
            dto.setDataRules(Collections.emptyMap());
            return dto;
        }

        // 3. 查 Permissions (RBAC)
        List<Long> permIds = rolePermRepo.listPermissionIdsByRoleIds(activeRoleIds);

        Set<String> perms = new HashSet<>();
        if (!permIds.isEmpty()) {
            perms = permRepo.listByIds(permIds).stream().map(SysPermission::getPermissionCode)
                // 过滤掉为空的 permCode (比如纯目录节点)
                .filter(c -> c != null && !c.trim().isEmpty()).collect(Collectors.toSet());
        }
        dto.setPermissions(perms);

        // 4. 查 Data Rules (ABAC)
        List<Long> ruleIds = roleDataRuleRepo.listDataRuleIdsByRoleIds(activeRoleIds);

        Map<String, List<SysDataRule>> rulesMap = new HashMap<>();
        if (!ruleIds.isEmpty()) {
            List<SysDataRule> dataRules = dataRuleRepo.listByIds(ruleIds);
            // 按照 targetResource (如 "DlyBuy") 分组，供 MyBatis 拦截器快速命中
            rulesMap = dataRules.stream().collect(Collectors.groupingBy(SysDataRule::getTargetResource));
        }
        dto.setDataRules(rulesMap);

        return dto;
    }

    private @NonNull UserAuthInfoDTO initAndLoadDto(SysUser user) {
        UserAuthInfoDTO dto = new UserAuthInfoDTO();
        dto.setId(user.getId());
        dto.setLoginAccount(user.getLoginAccount());
        dto.setDeptId(user.getDeptId());
        dto.setStatus(user.getStatus());
        dto.setPermissionVersion(user.getPermissionVersion());

        dto.setEmployeeTypeId(employeeAssocRepo.getTypeIdByLoginAccount(user.getLoginAccount()));
        dto.setDepartmentTypeId((ObjUtil.isEmpty(user.getDeptId()) || MAX_DEPT_ID.equals(user.getDeptId())) ? null
            : deptAssocRepo.getTypeIdByDeptId(user.getDeptId()));

        // 🌟 核心修改：查询部门状态
        if (user.getDeptId() != null) {
            SysDept dept = sysDeptRepo.getById(user.getDeptId());
            // 如果部门不存在，或部门状态为 0，则标记为不活跃
            dto.setDepartmentActive(dept != null && dept.getStatus() == 1);
        } else {
            // 无部门用户（如超管）默认活跃
            dto.setDepartmentActive(true);
        }

        return dto;
    }
}