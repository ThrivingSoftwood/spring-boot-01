// File: ./kaishi/src/main/java/thriving/softwood/kaishi/biz/api/sync/ErpSyncSvc.java
package thriving.softwood.kaishi.biz.api.sync;

import static thriving.softwood.kaishi.biz.constant.BaseConst.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;

import cn.hutool.v7.core.comparator.CompareUtil;
import cn.hutool.v7.core.date.DateUtil;
import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.crypto.digest.BCrypt;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysDept;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysDeptRepo;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysUserRepo;
import thriving.softwood.common.core.exception.DetailException;
import thriving.softwood.common.core.util.Sm4Util;
import thriving.softwood.kaishi.biz.pojo.record.ErpSyncReq;
import thriving.softwood.kaishi.biz.pojo.vo.DepartmentVO;
import thriving.softwood.kaishi.biz.pojo.vo.OrgNodeVO;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Department;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Employee;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.GblLoginUser;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo.DepartmentRepo;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo.EmployeeRepo;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo.GblLoginUserRepo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.entity.base.DepartmentAssociationInfo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.entity.base.EmployeeAssociationInfo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.DepartmentAssociationInfoRepo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.EmployeeAssociationInfoRepo;

@Service
public class ErpSyncSvc implements ErpSyncApi {

    // 引入 Auth 模块的 Repo
    private final SysUserRepo sysUserRepo;
    private final SysDeptRepo sysDeptRepo;

    // 引入 Kaishi 模块的 Repo
    private final DepartmentRepo erpDeptRepo;
    private final EmployeeRepo erpEmployeeRepo;
    private final GblLoginUserRepo gblLoginUserRepo;
    private final EmployeeAssociationInfoRepo empAssocRepo;
    private final DepartmentAssociationInfoRepo deptAssocRepo;

    public ErpSyncSvc(SysUserRepo sysUserRepo, SysDeptRepo sysDeptRepo, DepartmentRepo erpDeptRepo,
        EmployeeRepo erpEmployeeRepo, GblLoginUserRepo gblLoginUserRepo, EmployeeAssociationInfoRepo empAssocRepo,
        DepartmentAssociationInfoRepo deptAssocRepo) {
        this.sysUserRepo = sysUserRepo;
        this.sysDeptRepo = sysDeptRepo;
        this.erpDeptRepo = erpDeptRepo;
        this.erpEmployeeRepo = erpEmployeeRepo;
        this.gblLoginUserRepo = gblLoginUserRepo;
        this.empAssocRepo = empAssocRepo;
        this.deptAssocRepo = deptAssocRepo;
    }

    private static @NonNull List<OrgNodeVO> loadAllNodes(List<Department> erpDepartments, List<Employee> erpEmployees,
        Set<String> syncedEmpTypeIds) {
        List<OrgNodeVO> allNodes = new ArrayList<>();

        // 1. 转换 ERP 部门
        for (Department d : erpDepartments) {
            OrgNodeVO node = new OrgNodeVO();
            node.setId(DEPT_PREFIX + d.getTypeid());
            node.setParentId(ROOT_DEPARTMENT_ID_STR.equals(d.getParid()) ? "0" : (DEPT_PREFIX + d.getParid()));
            node.setName(d.getFullName());
            node.setNodeType(1);
            // 弹窗中部门不可勾选，只供展示层级
            // node.setDisabled(true);
            allNodes.add(node);
        }
        // 对于 Department 列未 null 或空串或空格的,增加其他部门
        OrgNodeVO others = new OrgNodeVO();
        others.setId(DEPT_PREFIX + MAX_DEPT_ID);
        others.setParentId("D_00000");
        others.setName("其他部门");
        others.setNodeType(1);
        allNodes.add(others);

        // 2. 转换未同步的 ERP 员工
        for (Employee e : erpEmployees) {
            if (!syncedEmpTypeIds.contains(e.getTypeId())) {
                OrgNodeVO node = new OrgNodeVO();
                node.setId(EMPLOYEE_PREFIX + e.getTypeId());
                // 员工关联的 ERP 部门 ID; 当 Department 为空时,填入"其他部门"
                node.setParentId(DEPT_PREFIX + (StrUtil.isBlank(e.getDepartment()) ? MAX_DEPT_ID : e.getDepartment()));
                node.setName(e.getFullName());
                node.setLoginCode(e.getUserCode());
                node.setSourceId(e.getTypeId());
                node.setNodeType(2);
                allNodes.add(node);
            }
        }
        return allNodes;
    }

    // ==========================================
    // 1. 同步人员逻辑
    // ==========================================
    @DSTransactional
    @Override
    public void syncUsers(ErpSyncReq req) {
        if (req.erpEmployeeTypeIds() == null || req.erpEmployeeTypeIds().isEmpty()) {
            return;
        }

        List<Employee> erpEmployees = erpEmployeeRepo.listByTypeIds(req.erpEmployeeTypeIds());
        Map<String, Long> deptMapping = deptAssocRepo.listAll().stream().collect(Collectors
            .toMap(DepartmentAssociationInfo::getOriDepartmentTypeid, DepartmentAssociationInfo::getAuthDepartmentId));

        String plainPwd = Sm4Util.decWeb(req.newPasswordEnc());
        String initEncPwd = BCrypt.hashpw(plainPwd, BCrypt.gensalt());
        String currentVersion = DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss");

        for (Employee erpEmployee : erpEmployees) {
            Long localDeptId = deptMapping.get(erpEmployee.getDepartment());
            if (StrUtil.isNotBlank(erpEmployee.getDepartment()) && localDeptId == null) {
                throw new DetailException("员工 [" + erpEmployee.getFullName() + "] 的部门未同步！");
            }
            if (empAssocRepo.countByEmployeeTypeId(erpEmployee.getTypeId()) > 0) {
                throw new DetailException("员工账号[" + erpEmployee.getUserCode() + "] 已存在！");
            }

            // A. 保存到 common-auth 的系统用户表
            SysUser user = new SysUser();
            user.setLoginAccount(erpEmployee.getUserCode());
            user.setUsername(erpEmployee.getFullName());
            user.setPassword(initEncPwd);
            user.setDeptId(localDeptId == null ? 99999L : localDeptId);
            user.setStatus((byte)1);
            user.setPermissionVersion(currentVersion);
            sysUserRepo.save(user); // 这里会生成 user.id

            // B. 保存到 kaishi 的关联映射表
            EmployeeAssociationInfo assoc = new EmployeeAssociationInfo();
            assoc.setLoginAccount(user.getLoginAccount());
            assoc.setOriFullname(erpEmployee.getFullName());
            assoc.setEmployeeTypeid(erpEmployee.getTypeId());
            assoc.setEmployeeUserCode(erpEmployee.getUserCode());

            GblLoginUser loginUser = gblLoginUserRepo.getByUserCode(erpEmployee.getUserCode());
            if (loginUser != null) {
                assoc.setLoginUserCode(loginUser.getUserCode());
                assoc.setLoginEmployeeTypeid(loginUser.getEtypeid());
            }
            empAssocRepo.save(assoc);
        }
    }

    // ==========================================
    // 2. 同步部门逻辑
    // ==========================================
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
        Department department = erpDeptRepo.getByTypeId(typeId);
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
        SysDept sysDept = new SysDept(parentDept, department.getFullName(), Integer.valueOf(department.getTypeid()));
        sysDeptRepo.save(sysDept);

        // 4. 保存关联关系并更新本地 Map，供后续循环使用
        DepartmentAssociationInfo association = new DepartmentAssociationInfo(sysDept.getId(), typeId);
        deptAssocRepo.save(association);
        associations.put(typeId, association);
    }

    private Map<String, DepartmentAssociationInfo> loadAssociations() {
        return deptAssocRepo.listAll().stream()
            .collect(Collectors.toMap(DepartmentAssociationInfo::getOriDepartmentTypeid, Function.identity(),
                (oldV, newV) -> CompareUtil.compare(oldV.getUpdateTime(), newV.getUpdateTime()) > 0 ? oldV : newV));
    }

    // ==========================================
    // 3. 获取未同步树形数据逻辑
    // ==========================================
    @Override
    public List<OrgNodeVO> treeUnsyncedUsers() {
        List<Department> erpDepartments = erpDeptRepo.listAll();

        // 🌟 获取 ERP 在职、未删除员工
        List<Employee> erpEmployees = erpEmployeeRepo.listAll();

        // 获取本地已关联的员工 TypeId 集合
        Set<String> syncedEmpTypeIds =
            empAssocRepo.listAll().stream().map(EmployeeAssociationInfo::getEmployeeTypeid).collect(Collectors.toSet());

        List<OrgNodeVO> allNodes = loadAllNodes(erpDepartments, erpEmployees, syncedEmpTypeIds);

        // 🌟 树形组装
        List<OrgNodeVO> fullTree = buildTree(allNodes, "0");

        // 🌟 核心算法：剪枝 (移除没有未同步员工的空闲部门分支)
        List<OrgNodeVO> tree = pruneEmptyDepartments(fullTree);

        // 2. 🌟 核心：自底向上计算总人数
        for (OrgNodeVO root : tree) {
            calculateTotalUserCount(root);
        }
        return tree;
    }

    /**
     * 递归计算部门总人数（当前部门人数 + 所有子部门人数）
     */
    private int calculateTotalUserCount(OrgNodeVO node) {
        int count = 0;

        // 如果当前是用户节点，计数为 1
        if (node.getNodeType() == 2) {
            count = 1;
        } else {
            // 如果是部门节点，递归累加子节点的人数
            for (OrgNodeVO child : node.getChildren()) {
                count += calculateTotalUserCount(child);
            }
        }

        node.setUserCount(count);
        return count;
    }

    /**
     * 🌟 剪枝算法：递归移除不包含用户的空闲部门分支 返回 true 表示该节点(或其子孙) 包含用户，需要保留。
     */
    private List<OrgNodeVO> pruneEmptyDepartments(List<OrgNodeVO> tree) {
        List<OrgNodeVO> result = new ArrayList<>();
        for (OrgNodeVO node : tree) {
            if (hasUserInSubTree(node)) {
                result.add(node);
            }
        }
        return result;
    }

    private boolean hasUserInSubTree(OrgNodeVO node) {
        if (node.getNodeType() == 2) {
            return true; // 找到了用户叶子节点
        }

        // 如果是部门节点，递归检查其子节点
        List<OrgNodeVO> validChildren = new ArrayList<>();
        boolean hasUser = false;
        for (OrgNodeVO child : node.getChildren()) {
            if (hasUserInSubTree(child)) {
                validChildren.add(child);
                hasUser = true;
            }
        }

        // 重新赋值过滤后的有效子节点
        node.setChildren(validChildren);
        return hasUser;
    }

    /**
     * O(N) 高效树形组装算法
     */
    private List<OrgNodeVO> buildTree(List<OrgNodeVO> nodes, String rootId) {
        Map<String, List<OrgNodeVO>> childrenMap =
            nodes.stream().collect(Collectors.groupingBy(OrgNodeVO::getParentId));

        nodes.forEach(node -> {
            List<OrgNodeVO> children = childrenMap.get(node.getId());
            if (children != null) {
                node.setChildren(children);
            }
        });

        return nodes.stream().filter(n -> rootId.equals(n.getParentId())).collect(Collectors.toList());
    }

    @Override
    public List<DepartmentVO> treeUnsyncedDepartments() {
        // 1. 获取管家婆全量数据
        List<Department> allDepartments = erpDeptRepo.listAll();
        // 2. 获取已同步 ID 集合
        Set<String> syncedIds = deptAssocRepo.listAll().stream().map(DepartmentAssociationInfo::getOriDepartmentTypeid)
            .collect(Collectors.toSet());

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
}