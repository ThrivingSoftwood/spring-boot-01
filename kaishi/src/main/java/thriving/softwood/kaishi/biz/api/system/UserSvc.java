package thriving.softwood.kaishi.biz.api.system;

import static thriving.softwood.kaishi.biz.constant.BaseConst.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;

import cn.hutool.v7.core.date.DateUtil;
import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.crypto.digest.BCrypt;
import thriving.softwood.common.core.exception.DetailException;
import thriving.softwood.common.core.util.Sm4Util;
import thriving.softwood.kaishi.biz.pojo.record.UserReq;
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
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDept;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysUserRole;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysDeptRepo;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysUserRepo;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysUserRoleRepo;

@Service
public class UserSvc implements UserApi {

    private final SysDeptRepo sysDeptRepo;
    private final SysUserRepo sysUserRepo;
    private final DepartmentRepo erpDeptRepo;
    private final EmployeeRepo erpEmployeeRepo;
    private final SysUserRoleRepo sysUserRoleRepo;
    private final GblLoginUserRepo gblLoginUserRepo;
    private final EmployeeAssociationInfoRepo empAssocRepo;
    private final DepartmentAssociationInfoRepo deptAssocRepo;

    public UserSvc(SysDeptRepo sysDeptRepo, SysUserRepo sysUserRepo, DepartmentRepo erpDeptRepo,
        EmployeeRepo erpEmployeeRepo, EmployeeAssociationInfoRepo empAssocRepo,
        DepartmentAssociationInfoRepo deptAssocRepo, GblLoginUserRepo gblLoginUserRepo,
        SysUserRoleRepo sysUserRoleRepo) {
        this.sysDeptRepo = sysDeptRepo;
        this.sysUserRepo = sysUserRepo;
        this.erpDeptRepo = erpDeptRepo;
        this.erpEmployeeRepo = erpEmployeeRepo;
        this.empAssocRepo = empAssocRepo;
        this.deptAssocRepo = deptAssocRepo;
        this.gblLoginUserRepo = gblLoginUserRepo;
        this.sysUserRoleRepo = sysUserRoleRepo;
    }

    // ==========================================
    // 1. 获取本地系统树 (部门 + 员工)
    // ==========================================
    @Override
    public List<OrgNodeVO> treeSynced() {
        // 1. 获取所有本地部门
        List<SysDept> departments = sysDeptRepo.listAll();

        // 2. 获取所有本地用户 (🌟 核心：排除上帝账号)
        List<SysUser> users = sysUserRepo.listAll();

        List<OrgNodeVO> allNodes = loadAllNodes(departments, users);

        List<OrgNodeVO> tree = buildTree(allNodes, "0");
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

    private static @NonNull List<OrgNodeVO> loadAllNodes(List<SysDept> departments, List<SysUser> users) {
        List<OrgNodeVO> allNodes = new ArrayList<>();

        // 3. 转换部门为 Node (nodeType = 1)
        for (SysDept d : departments) {
            OrgNodeVO node = new OrgNodeVO();
            node.setId(DEPT_PREFIX + d.getId());
            node.setParentId(d.getParentId() == 0 ? "0" : (DEPT_PREFIX + d.getParentId()));
            node.setName(d.getDeptName());
            node.setNodeType(1);
            node.setStatus(d.getStatus());
            // 部门节点不允许在用户管理里被操作
            node.setDisabled(true);
            allNodes.add(node);
        }
        // 对于 Department 列未 null 或空串或空格的,增加其他部门
        OrgNodeVO others = new OrgNodeVO();
        others.setId(DEPT_PREFIX + MAX_DEPT_ID);
        // 挂靠在根部门下
        others.setParentId("D_1");
        others.setName("其他部门");
        others.setNodeType(1);
        others.setStatus((byte)1);
        // 部门节点不允许在用户管理里被操作
        others.setDisabled(true);
        allNodes.add(others);

        // 4. 转换用户为 Node (nodeType = 2)
        for (SysUser u : users) {
            OrgNodeVO node = new OrgNodeVO();
            node.setId(USER_PREFIX + u.getId());
            node.setParentId(DEPT_PREFIX + u.getDeptId()); // 挂载到对应部门下
            node.setName(u.getUsername());
            node.setExtCode(u.getLoginAccount());
            node.setNodeType(2);
            node.setStatus(u.getStatus());
            allNodes.add(node);
        }
        return allNodes;
    }

    // ==========================================
    // 2. 获取管家婆未同步人员树 (部门 + ERP员工)
    // ==========================================
    @Override
    public List<OrgNodeVO> treeUnsynced() {
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
                node.setExtCode(e.getUserCode());
                node.setSourceId(e.getTypeId());
                node.setNodeType(2);
                allNodes.add(node);
            }
        }
        return allNodes;
    }

    // ==========================================
    // 3. 执行人员引入 (同步)
    // ==========================================
    @Override
    @DSTransactional
    public void sync(UserReq req) {
        if (req.erpEmployeeTypeIds() == null || req.erpEmployeeTypeIds().isEmpty()) {
            return;
        }

        // 1. 拉取选中的 ERP 员工数据
        List<Employee> erpEmployees = erpEmployeeRepo.listByTypeIds(req.erpEmployeeTypeIds());

        // 2. 拉取部门映射表 (管家婆 dept_typeid -> 本地 sys_dept_id)
        Map<String, Long> deptMapping = deptAssocRepo.listAll().stream().collect(Collectors
            .toMap(DepartmentAssociationInfo::getOriDepartmentTypeid, DepartmentAssociationInfo::getAuthDepartmentId));

        String plainPwd = Sm4Util.decWeb(req.newPasswordEnc());
        String initEncPwd = BCrypt.hashpw(Sm4Util.encLocal(plainPwd), BCrypt.gensalt());
        String currentVersion = DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss");

        for (Employee erpEmployee : erpEmployees) {
            // 🌟 强一致性校验：员工所在的管家婆部门，是否已经同步到本地？
            Long localDeptId = deptMapping.get(erpEmployee.getDepartment());
            if (StrUtil.isNotBlank(erpEmployee.getDepartment()) && null == localDeptId) {
                throw new DetailException("员工 [" + erpEmployee.getFullName() + "] 所在的部门尚未同步至系统，请先在部门管理中同步该部门！");
            }

            // 校验账号是否冲突 (查询关联表是否存在要素相同且 delete = 0 的记录)
            if (empAssocRepo.countByEmployeeTypeId(erpEmployee.getTypeId()) > 0) {
                throw new DetailException("员工账号 [" + erpEmployee.getUserCode() + "] 已存在本地系统，引发冲突！");
            }

            // A. 插入 SysUser
            SysUser user = loadNewUser(erpEmployee, initEncPwd, localDeptId, currentVersion);
            sysUserRepo.save(user);

            // B. 插入关联表
            EmployeeAssociationInfo assoc = loadEmployeeAssociation(erpEmployee, user);
            empAssocRepo.save(assoc);
        }
    }

    private @NonNull EmployeeAssociationInfo loadEmployeeAssociation(Employee erpEmployee, SysUser user) {
        EmployeeAssociationInfo assoc = new EmployeeAssociationInfo();
        // 注意,这个列不是从 T_GBL_LOGINUSER 表来的数据
        assoc.setLoginAccount(user.getLoginAccount());
        assoc.setOriFullname(erpEmployee.getFullName());
        assoc.setEmployeeTypeid(erpEmployee.getTypeId());
        assoc.setEmployeeUserCode(erpEmployee.getUserCode());
        GblLoginUser loginUser = gblLoginUserRepo.getByUserCode(erpEmployee.getUserCode());
        if (null != loginUser) {
            assoc.setLoginUserCode(loginUser.getUserCode());
            // 注意,这个列不是从 T_GBL_LOGINUSER 表来的数据, 下面一行代码做容错处理,怕误解业务导致写错
            assoc.setLoginAccount(user.getLoginAccount());
            assoc.setLoginEmployeeTypeid(loginUser.getEtypeid());
        }
        return assoc;
    }

    private static @NonNull SysUser loadNewUser(Employee erpEmployee, String initEncPwd, Long localDeptId,
        String currentVersion) {
        SysUser user = new SysUser();
        user.setLoginAccount(erpEmployee.getUserCode());
        user.setUsername(erpEmployee.getFullName());
        user.setPassword(initEncPwd);
        // 如果 localDeptId 是 null,说明 Employee 对象中的 Department 为 null, 使其对应为其他部门(99999)
        user.setDeptId(null == localDeptId ? MAX_DEPT_ID : localDeptId);
        user.setStatus((byte)1);
        user.setPermissionVersion(currentVersion);
        return user;
    }

    // ==========================================
    // 4. 更新基本信息 (状态、部门)
    // ==========================================
    @Override
    @DSTransactional
    public void updateUser(UserReq req) {
        SysUser user = getValidUser(req.id());

        if (req.status() != null) {
            user.setStatus(req.status());
        }
        if (req.deptId() != null) {
            user.setDeptId(req.deptId());
        }

        // 🌟 触发静默刷新防线：更新权限版本号
        user.setPermissionVersion(DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss"));
        sysUserRepo.updateById(user);
    }

    // ==========================================
    // 5. 重置密码
    // ==========================================
    @Override
    public void resetPassword(UserReq req) {
        SysUser user = getValidUser(req.id());

        // 解析前端传来的 SM4 密文，再用 BCrypt 加密入库
        String plainPwd = Sm4Util.decWeb(req.newPasswordEnc());
        user.setLastPassword(user.getPassword());
        user.setPassword(BCrypt.hashpw(plainPwd, BCrypt.gensalt()));

        // 🌟 密码变更，强制更新权限版本，旧 Token 鉴权时可根据业务需要进行拦截
        user.setPermissionVersion(DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss"));
        sysUserRepo.updateById(user);
    }

    // ==========================================
    // 6. 移除用户 (逻辑删除)
    // ==========================================
    @Override
    @DSTransactional
    public void deleteUser(Long id) {
        SysUser user = getValidUser(id);

        // 1. 逻辑删除主表
        sysUserRepo.logicDelete(id);

        // 2. 逻辑删除关联表
        empAssocRepo.logicDelete(user.getLoginAccount());

        // 3. 如果有 UserRole 表，也可以在这里一并清理关联，防止恢复时串权
        sysUserRoleRepo.logicDeleteByUserId(user.getId());
    }

    // --- 内部辅助方法 ---

    private SysUser getValidUser(Long id) {
        SysUser user = sysUserRepo.getById(id);
        if (user == null || "kaishi".equals(user.getLoginAccount())) {
            throw new RuntimeException("非法操作：目标用户不存在或属于系统保护级账号！");
        }
        return user;
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

    @Override
    public List<Long> listAssignedRoleIdsByUserId(Long userId) {
        // 🌟 直接从关联表查询该用户绑定的角色 ID 列表
        return sysUserRoleRepo.listRoleIdsByUserId(userId);
    }

    @Override
    @DSTransactional // 开启跨表事务
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 1. 安全校验：禁止修改上帝账号 'kaishi' 的角色
        SysUser user = sysUserRepo.getById(userId);
        if ("kaishi".equals(user.getLoginAccount())) {
            throw new DetailException("系统保护：禁止修改初始化管理员的角色！");
        }

        // 2. 物理清理旧关系
        sysUserRoleRepo.logicDeleteByUserId(userId);

        // 3. 批量插入新关系
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> relations = roleIds.stream().map(rid -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(rid);
                return ur;
            }).collect(Collectors.toList());
            sysUserRoleRepo.saveBatch(relations);
        }

        // 4. 🌟 核心防线：更新该用户的权限版本戳
        // 这样当该用户下次发起请求时，JwtInterceptor 会发现版本号变了，
        // 从而下发 X-Update-Perm 强制前端静默换取包含新角色权限的 Token。
        String newVersion = DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss");
        user.setPermissionVersion(newVersion);
        sysUserRepo.updateById(user);
    }
}