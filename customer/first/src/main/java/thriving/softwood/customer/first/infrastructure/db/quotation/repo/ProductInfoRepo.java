package thriving.softwood.customer.first.infrastructure.db.quotation.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.customer.first.infrastructure.db.quotation.entity.base.ProductInfo;
import thriving.softwood.customer.first.infrastructure.db.quotation.mapper.base.ProductInfoMapper;

/**
 * <p>
 * 商品信息表 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@DS("quotation")
@Service
public class ProductInfoRepo extends AncestorServiceImpl<ProductInfoMapper, ProductInfo> {

}
