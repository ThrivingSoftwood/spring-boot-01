package thriving.softwood.customer.first.infrastructure.db.quotation.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.customer.first.infrastructure.db.quotation.entity.base.ProductInfo;

/**
 * <p>
 * 商品信息表 Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@DS("quotation")
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {}
