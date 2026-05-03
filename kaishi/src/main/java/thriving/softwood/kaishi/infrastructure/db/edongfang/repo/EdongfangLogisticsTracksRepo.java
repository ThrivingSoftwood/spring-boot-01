package thriving.softwood.kaishi.infrastructure.db.edongfang.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangLogisticsTracks;
import thriving.softwood.kaishi.infrastructure.db.edongfang.mapper.base.EdongfangLogisticsTracksMapper;

/**
 * <p>
 * 物流轨迹 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
@Service
public class EdongfangLogisticsTracksRepo
    extends AncestorServiceImpl<EdongfangLogisticsTracksMapper, EdongfangLogisticsTracks> {

}
