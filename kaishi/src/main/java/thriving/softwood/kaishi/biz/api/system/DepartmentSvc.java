package thriving.softwood.kaishi.biz.api.system;

import static thriving.softwood.kaishi.biz.constant.BaseConst.ROOT_DEPARTMENT_ID_STR;
import static thriving.softwood.kaishi.biz.constant.BaseConst.ROOT_PARENT_DEPT_ID_LONG;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.v7.core.comparator.CompareUtil;
import cn.hutool.v7.core.util.ObjUtil;
import thriving.softwood.kaishi.biz.pojo.record.DepartmentReq;
import thriving.softwood.kaishi.biz.pojo.vo.DepartmentVO;
import thriving.softwood.kaishi.biz.pojo.vo.SysDeptVO;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Department;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo.DepartmentRepo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.entity.base.DepartmentAssociationInfo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.DepartmentAssociationInfoRepo;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDept;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysDeptRepo;
import thriving.softwood.kaishi.infrastructure.db.master.repo.SysUserRepo;

@Service
public class DepartmentSvc implements DepartmentApi {

    SysDeptRepo sysDeptRepo;
    DepartmentRepo departmentRepo;
    DepartmentAssociationInfoRepo associationRepo;
    SysUserRepo sysUserRepo;

    @Autowired
    public DepartmentSvc(SysDeptRepo sysDeptRepo, DepartmentRepo departmentRepo,
        DepartmentAssociationInfoRepo associationRepo, SysUserRepo sysUserRepo) {
        this.sysDeptRepo = sysDeptRepo;
        this.departmentRepo = departmentRepo;
        this.associationRepo = associationRepo;
        this.sysUserRepo = sysUserRepo;
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
            if (!vo.isSynced()) {
                // 如果自己没同步，溯源向上，把所有祖先都标记为“需保留”
                String currentId = vo.getTypeid();
                while (currentId != null && !"0".equals(currentId) && !keepIds.contains(currentId)) {
                    keepIds.add(currentId);
                    DepartmentVO parent = allNodesMap.get(allNodesMap.get(currentId).getParid());
                    currentId = (parent != null) ? parent.getTypeid() : null;
                }
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
    public void update(DepartmentReq departmentReq) {
        SysDept department = sysDeptRepo.getById(departmentReq.id());
        if (ObjUtil.notEquals(department.getSortOrder(), departmentReq.sortOrder())) {
            department.setSortOrder(departmentReq.sortOrder());
            sysDeptRepo.updateById(department);
        }
        if (ObjUtil.notEquals(department.getStatus(), departmentReq.status())) {
            // todo 递归处理子部门和相关人员权限版本,注意:无需处理其他部门的 sortOrder
        }
    }

    @Override
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
