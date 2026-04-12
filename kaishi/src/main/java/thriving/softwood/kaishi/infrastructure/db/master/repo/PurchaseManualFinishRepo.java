package thriving.softwood.kaishi.infrastructure.db.master.repo;

import thriving.softwood.kaishi.infrastructure.db.master.entity.PurchaseManualFinish;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.PurchaseManualFinishMapper;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.dynamic.datasource.annotation.DS;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author meta-thriving
* @since 2026-04-08
*/
@DS("master")
@Service
public class PurchaseManualFinishRepo extends AncestorServiceImpl<PurchaseManualFinishMapper, PurchaseManualFinish> {

}
