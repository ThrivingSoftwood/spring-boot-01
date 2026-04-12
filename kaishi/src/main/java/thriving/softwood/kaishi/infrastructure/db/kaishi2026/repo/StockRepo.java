package thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Stock;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.base.StockMapper;

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
public class StockRepo extends AncestorServiceImpl<StockMapper, Stock> {

    /**
     * 缓存时使用
     *
     * @return
     */
    public List<Stock> ListAllTypeidAndFullname() {
        LambdaQueryWrapper<Stock> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Stock::getTypeId, Stock::getFullName);
        return list(wrapper);
    }

    /**
     * 缓存时使用
     *
     * @return
     */
    public String getFullNameByTypeid(String typeid) {
        LambdaQueryWrapper<Stock> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Stock::getTypeId, typeid);
        return getOne(wrapper).getFullName();
    }

}
