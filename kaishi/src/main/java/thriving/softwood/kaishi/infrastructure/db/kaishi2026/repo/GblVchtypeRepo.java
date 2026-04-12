package thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.GblVchtype;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.base.GblVchtypeMapper;

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
public class GblVchtypeRepo extends AncestorServiceImpl<GblVchtypeMapper, GblVchtype> {

    /**
     * 缓存时使用
     *
     * @return
     */
    public List<GblVchtype> ListAllTypeidAndFullname() {
        LambdaQueryWrapper<GblVchtype> wrapper = Wrappers.lambdaQuery();
        wrapper.select(GblVchtype::getVchtype, GblVchtype::getFullname);
        return list(wrapper);
    }

    /**
     * 缓存时使用
     *
     * @return
     */
    public String getFullNameByVchType(Integer vchtype) {
        LambdaQueryWrapper<GblVchtype> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(GblVchtype::getVchtype, vchtype);
        return getOne(wrapper).getFullname();
    }

}
