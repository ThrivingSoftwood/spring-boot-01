package thriving.softwood.customer.first.infrastructure.db.edongfang.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangOrders;

/**
 * <p>
 * 订单创建主表 Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
public interface EdongfangOrdersMapper extends BaseMapper<EdongfangOrders> {}
