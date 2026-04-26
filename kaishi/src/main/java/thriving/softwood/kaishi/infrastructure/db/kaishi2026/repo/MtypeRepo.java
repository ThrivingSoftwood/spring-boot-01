package thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Mtype;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.base.MtypeMapper;

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
public class MtypeRepo extends AncestorServiceImpl<MtypeMapper, Mtype> {

    /**
     * 缓存时使用
     *
     * @return
     */
    public List<Mtype> ListAllTypeidAndFullname() {
        LambdaQueryWrapper<Mtype> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Mtype::getTypeid, Mtype::getFullName);
        return list(wrapper);
    }

    /**
     * 缓存时使用
     *
     * @return
     */
    public String getFullNameByTypeid(String typeid) {
        LambdaQueryWrapper<Mtype> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Mtype::getTypeid, typeid);
        return getOne(wrapper).getFullName();
    }

}
