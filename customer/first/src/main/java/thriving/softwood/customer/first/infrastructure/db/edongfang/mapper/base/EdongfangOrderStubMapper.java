package thriving.softwood.customer.first.infrastructure.db.edongfang.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangOrderStub;

/**
 * <p>
 * 订单生命周期状态存根与通知判定凭据表 Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-29
 */
@DS("edongfang")
public interface EdongfangOrderStubMapper extends BaseMapper<EdongfangOrderStub> {}
