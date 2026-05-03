package thriving.softwood.common.auth.api.system;

import static thriving.softwood.common.auth.constant.BaseConst.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;

import cn.hutool.v7.core.date.DateUtil;
import cn.hutool.v7.crypto.digest.BCrypt;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysDept;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysUserRole;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysDeptRepo;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysUserRepo;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysUserRoleRepo;
import thriving.softwood.common.auth.pojo.record.UserReq;
import thriving.softwood.common.auth.pojo.vo.OrganizationNodeVO;
import thriving.softwood.common.auth.spi.AuthBusinessProvider;
import thriving.softwood.common.core.exception.DetailException;
import thriving.softwood.common.core.util.Sm4Util;

@Service
public class UserSvc implements UserApi {

    private final SysDeptRepo sysDeptRepo;
    private final SysUserRepo sysUserRepo;
    private final SysUserRoleRepo sysUserRoleRepo;
    private final AuthBusinessProvider bizProvider;

    public UserSvc(SysDeptRepo sysDeptRepo, SysUserRepo sysUserRepo, SysUserRoleRepo sysUserRoleRepo,
        AuthBusinessProvider bizProvider) {
        this.sysDeptRepo = sysDeptRepo;
        this.sysUserRepo = sysUserRepo;
        this.sysUserRoleRepo = sysUserRoleRepo;
        this.bizProvider = bizProvider;
    }

    private static @NonNull List<OrganizationNodeVO> loadAllNodes(List<SysDept> departments, List<SysUser> users) {
        List<OrganizationNodeVO> allNodes = new ArrayList<>();

        // 3. 转换部门为 Node (nodeType = 1)
        for (SysDept d : departments) {
            OrganizationNodeVO node = new OrganizationNodeVO();
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
        OrganizationNodeVO others = new OrganizationNodeVO();
        others.setId(DEPT_PREFIX + MAX_DEPT_ID);
        // 挂靠在根部门下，先写死为获取第一个元素的 id
        others.setParentId(allNodes.getFirst().getId());
        others.setName("其他部门");
        others.setNodeType(1);
        others.setStatus((byte)1);
        // 部门节点不允许在用户管理里被操作
        others.setDisabled(true);
        allNodes.add(others);

        // 4. 转换用户为 Node (nodeType = 2)
        for (SysUser u : users) {
            OrganizationNodeVO node = new OrganizationNodeVO();
            node.setId(USER_PREFIX + u.getId());
            node.setParentId(DEPT_PREFIX + u.getDeptId()); // 挂载到对应部门下
            node.setName(u.getUsername());
            node.setLoginCode(u.getLoginAccount());
            node.setNodeType(2);
            node.setStatus(u.getStatus());
            allNodes.add(node);
        }
        return allNodes;
    }

    // ==========================================
    // 1. 获取本地系统树 (部门 + 员工)
    // ==========================================
    @Override
    public List<OrganizationNodeVO> treeSynced() {
        // 1. 获取所有本地部门
        List<SysDept> departments = sysDeptRepo.listAll();

        // 2. 获取所有本地用户 (🌟 核心：排除上帝账号)
        List<SysUser> users = sysUserRepo.listAll();

        List<OrganizationNodeVO> allNodes = loadAllNodes(departments, users);

        List<OrganizationNodeVO> tree = buildTree(allNodes, "0");
        // 2. 🌟 核心：自底向上计算总人数
        for (OrganizationNodeVO root : tree) {
            calculateTotalUserCount(root);
        }
        return tree;
    }

    /**
     * 递归计算部门总人数（当前部门人数 + 所有子部门人数）
     */
    private int calculateTotalUserCount(OrganizationNodeVO node) {
        int count = 0;

        // 如果当前是用户节点，计数为 1
        if (node.getNodeType() == 2) {
            count = 1;
        } else {
            // 如果是部门节点，递归累加子节点的人数
            for (OrganizationNodeVO child : node.getChildren()) {
                count += calculateTotalUserCount(child);
            }
        }

        node.setUserCount(count);
        return count;
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

        // 先删除关联信息再删主信息
        bizProvider.associateDelete(id);

        // 1. 逻辑删除主表
        sysUserRepo.logicDelete(id);

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
    private List<OrganizationNodeVO> buildTree(List<OrganizationNodeVO> nodes, String rootId) {
        Map<String, List<OrganizationNodeVO>> childrenMap =
            nodes.stream().collect(Collectors.groupingBy(OrganizationNodeVO::getParentId));
        nodes.forEach(node -> {
            List<OrganizationNodeVO> children = childrenMap.get(node.getId());
            if (children != null) {
                node.setChildren(children);
            }
        });

        return nodes.stream().filter(n -> rootId.equals(n.getParentId())).collect(Collectors.toList());
    }

    /**
     * 🌟 剪枝算法：递归移除不包含用户的空闲部门分支 返回 true 表示该节点(或其子孙) 包含用户，需要保留。
     */
    private List<OrganizationNodeVO> pruneEmptyDepartments(List<OrganizationNodeVO> tree) {
        List<OrganizationNodeVO> result = new ArrayList<>();
        for (OrganizationNodeVO node : tree) {
            if (hasUserInSubTree(node)) {
                result.add(node);
            }
        }
        return result;
    }

    private boolean hasUserInSubTree(OrganizationNodeVO node) {
        if (node.getNodeType() == 2) {
            return true; // 找到了用户叶子节点
        }

        // 如果是部门节点，递归检查其子节点
        List<OrganizationNodeVO> validChildren = new ArrayList<>();
        boolean hasUser = false;
        for (OrganizationNodeVO child : node.getChildren()) {
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