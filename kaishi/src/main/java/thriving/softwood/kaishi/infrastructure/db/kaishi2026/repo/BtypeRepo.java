package thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Btype;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.base.BtypeMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@DS("kaishi-2026")
@Service
public class BtypeRepo extends AncestorServiceImpl<BtypeMapper, Btype> {

    /**
     * 缓存时使用
     * 
     * @return
     */
    public List<Btype> ListAllTypeidAndFullname() {
        LambdaQueryWrapper<Btype> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Btype::getTypeId, Btype::getFullName);
        return list(wrapper);
    }

    /**
     * 缓存时使用
     *
     * @return
     */
    public String getFullNameByTypeid(String typeid) {
        LambdaQueryWrapper<Btype> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Btype::getTypeId, typeid);
        return getOne(wrapper).getFullName();
    }

}
