package thriving.softwood.customer.first.infrastructure.db.kaishi2026.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.customer.first.infrastructure.db.kaishi2026.entity.base.Dlystock;
import thriving.softwood.customer.first.infrastructure.db.kaishi2026.mapper.base.DlystockMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@DS("cust0001-2026")
@Service
public class DlystockRepo extends AncestorServiceImpl<DlystockMapper, Dlystock> {}
