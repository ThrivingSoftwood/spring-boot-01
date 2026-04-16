package thriving.softwood.kaishi.infrastructure.db.master.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDept;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysDeptMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-15
 */
@DS("master")
@Service
public class SysDeptRepo extends AncestorServiceImpl<SysDeptMapper, SysDept> {

    public List<SysDept> listAll() {
        return lambdaQuery().eq(SysDept::getDeleted, 0).orderByAsc(SysDept::getParentId, SysDept::getSortOrder).list();
    }

    public Long countSubDept(Long id) {
        return lambdaQuery().eq(SysDept::getParentId, id).eq(SysDept::getDeleted, 0).count();
    }

    public Boolean logicDelete(Long id) {
        return lambdaUpdate().set(SysDept::getDeleted, 1).eq(SysDept::getId, id).update();
    }
}
