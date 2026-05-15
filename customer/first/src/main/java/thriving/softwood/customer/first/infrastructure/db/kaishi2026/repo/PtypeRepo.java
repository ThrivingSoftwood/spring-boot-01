package thriving.softwood.customer.first.infrastructure.db.kaishi2026.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.customer.first.infrastructure.db.kaishi2026.entity.base.Ptype;
import thriving.softwood.customer.first.infrastructure.db.kaishi2026.mapper.base.PtypeMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@DS("cust0001-2026")
@Service
public class PtypeRepo extends AncestorServiceImpl<PtypeMapper, Ptype> {

    /**
     * 缓存时使用
     *
     * @return
     */
    public List<Ptype> ListAllTypeidAndFullname() {
        LambdaQueryWrapper<Ptype> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Ptype::getTypeId, Ptype::getFullName);
        return list(wrapper);
    }

    /**
     * 缓存时使用
     *
     * @return
     */
    public String getFullNameByTypeid(String typeid) {
        LambdaQueryWrapper<Ptype> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Ptype::getTypeId, typeid);
        return getOne(wrapper).getFullName();
    }

}
