package thriving.softwood.kaishi.infrastructure.db.edongfang.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangInvoices;
import thriving.softwood.kaishi.infrastructure.db.edongfang.mapper.base.EdongfangInvoicesMapper;

/**
 * <p>
 * 开票申请表 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
@Service
public class EdongfangInvoicesRepo extends AncestorServiceImpl<EdongfangInvoicesMapper, EdongfangInvoices> {

}
