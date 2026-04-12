package thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Department;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.base.DepartmentMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-06
 */
@DS("kaishi-2026")
@Service
public class DepartmentRepo extends AncestorServiceImpl<DepartmentMapper, Department> {

    /**
     * 缓存时使用
     *
     * @return
     */
    public List<Department> ListAllTypeidAndFullname() {
        LambdaQueryWrapper<Department> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Department::getTypeid, Department::getFullName);
        return list(wrapper);
    }

    /**
     * 缓存时使用
     *
     * @return
     */
    public String getFullNameByTypeid(String typeid) {
        LambdaQueryWrapper<Department> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Department::getTypeid, typeid);
        return getOne(wrapper).getFullName();
    }

}
