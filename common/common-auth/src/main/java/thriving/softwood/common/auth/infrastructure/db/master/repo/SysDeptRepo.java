package thriving.softwood.common.auth.infrastructure.db.master.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysDept;
import thriving.softwood.common.auth.infrastructure.db.master.mapper.base.SysDeptMapper;
import thriving.softwood.common.auth.infrastructure.db.master.mapper.extend.SysDeptExtendMapper;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-15
 */
@DS("master")
@Service
public class SysDeptRepo extends AncestorServiceImpl<SysDeptMapper, SysDept> {

    private SysDeptExtendMapper extendMapper;

    @Autowired
    public SysDeptRepo(SysDeptExtendMapper extendMapper) {
        this.extendMapper = extendMapper;
    }

    public List<SysDept> listAll() {
        return lambdaQuery().orderByAsc(SysDept::getParentId, SysDept::getSortOrder).list();
    }

    public Long countSubDept(Long id) {
        return lambdaQuery().eq(SysDept::getParentId, id).count();
    }

    public Boolean logicDelete(Long id) {
        return lambdaUpdate().set(SysDept::getDeleted, 1).eq(SysDept::getId, id).update();
    }

    public List<SysDept> listAffectedSubDepartmentsByDeptId(Long deptId) {
        return extendMapper.listDeptWithChildren(deptId);
    }

    public void updateStatusByDeptIds(byte targetStatus, List<Long> affectedDeptIds) {
        lambdaUpdate().set(SysDept::getStatus, targetStatus).in(SysDept::getId, affectedDeptIds).update();
    }

    public long countDifferentStatusBrotherDeptCount(Long parentId, byte targetStatus) {
        return lambdaQuery().eq(SysDept::getParentId, parentId).ne(SysDept::getStatus, targetStatus).count();
    }

    public void updateStatusByDeptId(Long parentId, byte targetStatus) {
        lambdaUpdate().set(SysDept::getStatus, targetStatus).eq(SysDept::getId, parentId).update();
    }
}
