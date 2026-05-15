package thriving.softwood.customer.first.infrastructure.db.edongfang.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangProductStocks;
import thriving.softwood.customer.first.infrastructure.db.edongfang.mapper.base.EdongfangProductStocksMapper;

/**
 * <p>
 * 获取商品库存接口返回数据 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
@Service
public class EdongfangProductStocksRepo
    extends AncestorServiceImpl<EdongfangProductStocksMapper, EdongfangProductStocks> {

}
