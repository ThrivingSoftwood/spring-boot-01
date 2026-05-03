package thriving.softwood.kaishi.infrastructure.db.edongfang.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangProductPrices;
import thriving.softwood.kaishi.infrastructure.db.edongfang.mapper.base.EdongfangProductPricesMapper;

/**
 * <p>
 * 商品价格表 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
@Service
public class EdongfangProductPricesRepo
    extends AncestorServiceImpl<EdongfangProductPricesMapper, EdongfangProductPrices> {

}
