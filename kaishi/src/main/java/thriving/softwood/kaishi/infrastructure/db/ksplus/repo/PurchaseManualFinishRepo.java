package thriving.softwood.kaishi.infrastructure.db.ksplus.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.ksplus.entity.base.PurchaseManualFinish;
import thriving.softwood.kaishi.infrastructure.db.ksplus.mapper.base.PurchaseManualFinishMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-08
 */
@DS("ksplus")
@Service
public class PurchaseManualFinishRepo extends AncestorServiceImpl<PurchaseManualFinishMapper, PurchaseManualFinish> {

}
