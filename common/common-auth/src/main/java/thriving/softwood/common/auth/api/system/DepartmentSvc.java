package thriving.softwood.common.auth.api.system;

import static thriving.softwood.common.auth.config.AncestorAuthConfig.AUTH_CACHE_MANAGER;
import static thriving.softwood.common.auth.config.AncestorAuthConfig.USER_AUTH_INFO_CACHE;
import static thriving.softwood.common.auth.constant.BaseConst.ROOT_PARENT_DEPT_ID_LONG;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;

import cn.hutool.v7.core.date.DateUtil;
import cn.hutool.v7.core.util.ObjUtil;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysDept;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysDeptRepo;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysUserRepo;
import thriving.softwood.common.auth.pojo.record.DepartmentReq;
import thriving.softwood.common.auth.pojo.vo.SysDeptVO;
import thriving.softwood.common.auth.spi.AuthBusinessProvider;

@Service
public class DepartmentSvc implements DepartmentApi {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentSvc.class);
    private final CacheManager cacheManager;

    SysDeptRepo sysDeptRepo;
    SysUserRepo sysUserRepo;
    AuthBusinessProvider bizProvider;

    @Autowired
    public DepartmentSvc(SysDeptRepo sysDeptRepo, SysUserRepo sysUserRepo,
        @Qualifier(AUTH_CACHE_MANAGER) CacheManager cacheManager, AuthBusinessProvider bizProvider) {
        this.sysDeptRepo = sysDeptRepo;
        this.sysUserRepo = sysUserRepo;
        this.cacheManager = cacheManager;
        this.bizProvider = bizProvider;
    }

    private static @NonNull List<SysDeptVO> getTreedVOs(List<SysDept> departments, Map<Long, String> associations) {
        // 2. 将 Entity 转换为 VO
        List<SysDeptVO> allVOs = departments.stream().map(department -> {
            // 调用构造函数创建 VO
            return new SysDeptVO(department, associations.get(department.getId()));
        }).collect(Collectors.toList());

        // 3. 按照 parentId 分组 (极其高效的 Java Stream API)
        Map<Long, List<SysDeptVO>> childrenMap = allVOs.stream().collect(Collectors.groupingBy(SysDeptVO::getParentId));

        // 4. 遍历所有节点，将子节点塞入对应的父节点中
        allVOs.forEach(node -> {
            List<SysDeptVO> children = childrenMap.get(node.getId());
            if (children != null) {
                node.setChildren(children);
            }
        });
        return allVOs;
    }

    @Override
    public List<SysDeptVO> treeDepartments() {
        List<SysDept> departments = sysDeptRepo.listAll();
        Map<Long, String> associations = bizProvider.loadAssocDeptIds();

        return getTreedVOs(departments, associations).stream()
            .filter(node -> node.getParentId() == ROOT_PARENT_DEPT_ID_LONG).collect(Collectors.toList());
    }

    @Override
    @DSTransactional
    public void update(DepartmentReq departmentReq) {
        SysDept department = sysDeptRepo.getById(departmentReq.id());
        if (ObjUtil.notEquals(department.getSortOrder(), departmentReq.sortOrder())) {
            department.setSortOrder(departmentReq.sortOrder());
            sysDeptRepo.updateById(department);
        }
        if (ObjUtil.notEquals(department.getStatus(), departmentReq.status())) {
            changeStatusAndReloadPermission(departmentReq, department);
        }
    }

    private void changeStatusAndReloadPermission(DepartmentReq departmentReq, SysDept department) {
        byte targetStatus = departmentReq.status();
        Long deptId = department.getId();

        // A. 定位所有受影响的部门：包括当前部门及其所有子孙部门
        // 利用 SQL Server 的 LIKE 匹配 ancestors 字段，例如 ancestors 包含 ",5,"
        List<SysDept> affectedDepartments = sysDeptRepo.listAffectedSubDepartmentsByDeptId(deptId);

        if (affectedDepartments.isEmpty()) {
            return;
        }

        List<Long> affectedDeptIds = affectedDepartments.stream().map(SysDept::getId).collect(Collectors.toList());

        // B. 批量更新部门状态
        sysDeptRepo.updateStatusByDeptIds(targetStatus, affectedDeptIds);

        // B. 🌟 核心新增：自底向上传播状态
        propagateStatusUpwards(department.getParentId(), targetStatus, affectedDeptIds);

        // C. 🌟 权限版本碰撞：找出属于这些部门的所有用户
        List<SysUser> affectedUsers = sysUserRepo.listByDeptIds(affectedDeptIds);
        if (!affectedUsers.isEmpty()) {
            bumpUserVersions(affectedUsers);
        }
        logger.info("部门 {} 状态变更为 {}，已强制刷新 {} 名用户的权限版本", department.getDeptName(), targetStatus, affectedUsers.size());
    }

    private void bumpUserVersions(List<SysUser> affectedUsers) {

        // 生成全新的版本戳 (yyyyMMddHHmmss)
        String newVersion = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        List<Long> userIds = affectedUsers.stream().map(SysUser::getId).toList();

        // 批量更新用户的权限版本号
        // 这会导致受影响的用户在下一次请求时，JwtInterceptor 捕获到版本不一致
        sysUserRepo.updatePermissionVersionsByUserIds(newVersion, userIds);

        // D. 物理清理本地 Caffeine 缓存
        // 确保后端拦截器在 60s 内不必等待自然过期，而是瞬间感知部门状态变更
        Cache userAuthInfoCache = cacheManager.getCache(USER_AUTH_INFO_CACHE);
        if (userAuthInfoCache != null) {
            userIds.forEach(userAuthInfoCache::evict);
        }
    }

    /**
     * 🚀 向上递归判定逻辑
     *
     * @param parentId 待判定的父部门ID
     * @param targetStatus 目标状态
     * @param allAffectedDeptIds 受影响集合
     */
    private void propagateStatusUpwards(Long parentId, byte targetStatus, List<Long> allAffectedDeptIds) {
        // 1. 递归终止条件：到达根节点 (parentId=0)
        if (parentId == null || parentId == 0) {
            return;
        }

        // 2. 核心检查：该父部门下的所有子部门，是否都已经是目标状态？
        // 这里利用 count 检查是否存在“非目标状态”的子部门
        long nonTargetCount = sysDeptRepo.countDifferentStatusBrotherDeptCount(parentId, targetStatus);

        if (nonTargetCount > 0) {
            return;
        }

        // 3. 如果所有弟弟妹妹（子部门）都达标了
        SysDept parent = sysDeptRepo.getById(parentId);
        if (parent != null && parent.getStatus() != targetStatus) {
            // 更新父部门状态
            sysDeptRepo.updateStatusByDeptId(parentId, targetStatus);

            allAffectedDeptIds.add(parentId);
            logger.info("检测到子部门全员同步，父部门 {} 状态已联动修改为 {}", parent.getDeptName(), targetStatus);

            // 🌟 递归向上：继续检查爷爷节点
            propagateStatusUpwards(parent.getParentId(), targetStatus, allAffectedDeptIds);
        }

    }

    @Override
    @DSTransactional
    public String delete(DepartmentReq departmentReq) {
        if (sysDeptRepo.countSubDept(departmentReq.id()) > 0) {
            throw new RuntimeException("该部门存在未删除的下属部门,请检查!");
        }
        if (sysUserRepo.countUsers(departmentReq.id()) > 0) {
            throw new RuntimeException("该部门下存在已分配的用户,请检查!");
        }
        if (!sysDeptRepo.logicDelete(departmentReq.id())) {
            return "该部门不存在!";
        }
        bizProvider.associateDelete(departmentReq);
        return "删除成功!";
    }
}
