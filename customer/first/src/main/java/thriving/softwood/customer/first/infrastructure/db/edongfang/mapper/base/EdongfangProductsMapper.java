package thriving.softwood.customer.first.infrastructure.db.edongfang.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangProducts;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
public interface EdongfangProductsMapper extends BaseMapper<EdongfangProducts> {}
