package thriving.softwood.customer.first.infrastructure.db.edongfang.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangProductStocks;

/**
 * <p>
 * 获取商品库存接口返回数据 Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
public interface EdongfangProductStocksMapper extends BaseMapper<EdongfangProductStocks> {}
