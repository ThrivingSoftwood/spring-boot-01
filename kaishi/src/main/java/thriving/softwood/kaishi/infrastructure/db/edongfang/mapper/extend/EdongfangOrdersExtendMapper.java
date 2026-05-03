package thriving.softwood.kaishi.infrastructure.db.edongfang.mapper.extend;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import thriving.softwood.kaishi.biz.pojo.dto.EdongfangOrderDTO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrders;

/**
 * <p>
 * 订单创建主表 Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
public interface EdongfangOrdersExtendMapper extends BaseMapper<EdongfangOrders> {
    List<EdongfangOrderDTO> listUnnotified(@Param("maxOrderId") String maxOrderId);
}
