package thriving.softwood.kaishi.biz.api.system;

import static thriving.softwood.kaishi.biz.constant.BaseConst.ROOT_DEPARTMENT_ID_STR;
import static thriving.softwood.kaishi.biz.constant.BaseConst.ROOT_PARENT_DEPT_ID_LONG;
import static thriving.softwood.kaishi.infrastructure.cache.local.KaishiCaffeineCacheConfig.USER_AUTH_INFO_CACHE;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;

import cn.hutool.v7.core.comparator.CompareUtil;
import cn.hutool.v7.core.date.DateUtil;
import cn.hutool.v7.core.util.ObjUtil;
import thriving.softwood.kaishi.biz.pojo.record.DepartmentReq;
import thriving.softwood.kaishi.biz.pojo.vo.DepartmentVO;
import thriving.softwood.kaishi.biz.pojo.vo.SysDeptVO;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Department;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo.DepartmentRepo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.entity.base.DepartmentAssociationInfo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.DepartmentAssociationInfoRepo;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDept;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysDeptRepo;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysUserRepo;

@Service
public class DepartmentSvc implements DepartmentApi {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentSvc.class);
    private final CacheManager cacheManager;

    SysDeptRepo sysDeptRepo;
    DepartmentRepo departmentRepo;
    DepartmentAssociationInfoRepo associationRepo;
    SysUserRepo sysUserRepo;

    @Autowired
    public DepartmentSvc(SysDeptRepo sysDeptRepo, DepartmentRepo departmentRepo,
        DepartmentAssociationInfoRepo associationRepo, SysUserRepo sysUserRepo, CaffeineCacheManager cacheManager) {
        this.sysDeptRepo = sysDeptRepo;
        this.departmentRepo = departmentRepo;
        this.associationRepo = associationRepo;
        this.sysUserRepo = sysUserRepo;
        this.cacheManager = cacheManager;
    }

    @Override
    public List<SysDeptVO> treeDepartments() {
        List<SysDept> departments = sysDeptRepo.listAll();
        Map<Long,
            DepartmentAssociationInfo> associations = associationRepo.listAll().stream()
                .collect(Collectors.toMap(DepartmentAssociationInfo::getAuthDepartmentId, Function.identity(),
                    (oldValue, newValue) -> CompareUtil.compare(oldValue.getUpdateTime(), newValue.getUpdateTime()) > 0
                        ? oldValue : newValue));

        return getTreedVOs(departments, associations).stream()
            .filter(node -> node.getParentId() == ROOT_PARENT_DEPT_ID_LONG).collect(Collectors.toList());
    }

    private static @NonNull List<SysDeptVO> getTreedVOs(List<SysDept> departments,
        Map<Long, DepartmentAssociationInfo> associations) {
        // 2. 将 Entity 转换为 VO
        List<SysDeptVO> allVOs = departments.stream().map(department -> {
            // 根据当前部门的 ID 查找对应的关联信息
            DepartmentAssociationInfo info = associations.get(department.getId());
            // 调用构造函数创建 VO
            return new SysDeptVO(department, info);
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
    public List<DepartmentVO> treeUnsyncedDepartments() {
        // 1. 获取管家婆全量数据
        List<Department> allDepartments = departmentRepo.listAll();
        // 2. 获取已同步 ID 集合
        Set<String> syncedIds = associationRepo.listAll().stream()
            .map(DepartmentAssociationInfo::getOriDepartmentTypeid).collect(Collectors.toSet());

        // 3. 构建全量 Map
        Map<String, DepartmentVO> allNodesMap =
            allDepartments.stream().map(d -> new DepartmentVO(d, syncedIds.contains(d.getTypeid())))
                .collect(Collectors.toMap(DepartmentVO::getTypeid, Function.identity()));

        // 🌟 核心逻辑：标记哪些节点需要显示 (自身未同步，或子孙中有未同步的)
        Set<String> keepIds = new HashSet<>();
        for (DepartmentVO vo : allNodesMap.values()) {
            if (vo.isSynced()) {
                continue;
            }
            // 如果自己没同步，溯源向上，把所有祖先都标记为“需保留”
            String currentId = vo.getTypeid();
            while (currentId != null && !"0".equals(currentId) && !keepIds.contains(currentId)) {
                keepIds.add(currentId);
                DepartmentVO parent = allNodesMap.get(allNodesMap.get(currentId).getParid());
                currentId = (parent != null) ? parent.getTypeid() : null;
            }

        }

        // 4. 组装树结构
        List<DepartmentVO> rootNodes = new ArrayList<>();
        for (DepartmentVO node : allNodesMap.values()) {
            // 只处理需要保留的节点
            if (keepIds.contains(node.getTypeid())) {
                String parId = node.getParid();
                if ("0".equals(parId)) {
                    rootNodes.add(node);
                } else if (allNodesMap.containsKey(parId)) {
                    allNodesMap.get(parId).getChildren().add(node);
                }
            }
        }
        return rootNodes;
    }

    @Override
    @DSTransactional
    public void syncDepartments(List<String> typeIds) {
        // 1. 初始化全量 Map 仅一次，避免递归重复拉取
        Map<String, DepartmentAssociationInfo> associations = loadAssociations();

        for (String typeId : typeIds) {
            syncSingleDepartment(typeId, typeIds, associations);
        }
    }

    /**
     * 递归同步单个部门及其父级
     */
    private void syncSingleDepartment(String typeId, List<String> batchTypeIds,
        Map<String, DepartmentAssociationInfo> associations) {
        // 如果已经同步过，直接返回
        if (associations.containsKey(typeId)) {
            return;
        }

        // 同步父级到 ROOT_DEPARTMENT_ID_STR 时肯定查询不上来
        Department department = departmentRepo.getByTypeId(typeId);
        if (department == null) {
            return;
        }

        String parId = department.getParid();
        boolean isRoot = ROOT_DEPARTMENT_ID_STR.equals(parId);

        // 2. 处理父级同步逻辑
        if (!isRoot && !associations.containsKey(parId)) {
            // 如果父级也不在当前同步列表中，且数据库也没关联，则无法同步（数据异常）
            if (!batchTypeIds.contains(parId)) {
                throw new RuntimeException("上级部门未同步且不在本次任务中! 当前部门: " + department.getFullName());
            }
            // 递归同步父级
            syncSingleDepartment(parId, batchTypeIds, associations);
        }

        // 3. 构建并保存
        SysDept parentDept = null;
        if (!isRoot) {
            // 此时父级一定已存在于 associations 中（得益于上面的递归或初始加载）
            DepartmentAssociationInfo parentAssoc = associations.get(parId);
            if (parentAssoc != null) {
                parentDept = sysDeptRepo.getById(parentAssoc.getAuthDepartmentId());
            }
        }

        // 创建并保存部门
        SysDept sysDept = new SysDept(parentDept, department);
        sysDeptRepo.save(sysDept);

        // 4. 保存关联关系并更新本地 Map，供后续循环使用
        DepartmentAssociationInfo association = new DepartmentAssociationInfo(sysDept.getId(), typeId);
        associationRepo.save(association);
        associations.put(typeId, association);
    }

    private Map<String, DepartmentAssociationInfo> loadAssociations() {
        return associationRepo.listAll().stream()
            .collect(Collectors.toMap(DepartmentAssociationInfo::getOriDepartmentTypeid, Function.identity(),
                (oldV, newV) -> CompareUtil.compare(oldV.getUpdateTime(), newV.getUpdateTime()) > 0 ? oldV : newV));
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
        associationRepo.logicDelete(departmentReq.id());
        return "删除成功!";
    }
}
