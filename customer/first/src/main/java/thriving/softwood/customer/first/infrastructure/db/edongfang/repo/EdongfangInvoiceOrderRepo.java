package thriving.softwood.customer.first.infrastructure.db.edongfang.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangInvoiceOrder;
import thriving.softwood.customer.first.infrastructure.db.edongfang.mapper.base.EdongfangInvoiceOrderMapper;

/**
 * <p>
 * 发票订单关联 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
@Service
public class EdongfangInvoiceOrderRepo extends AncestorServiceImpl<EdongfangInvoiceOrderMapper, EdongfangInvoiceOrder> {

}
