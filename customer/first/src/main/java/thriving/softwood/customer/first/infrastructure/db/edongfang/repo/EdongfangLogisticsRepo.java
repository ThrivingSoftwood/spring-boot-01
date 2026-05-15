package thriving.softwood.customer.first.infrastructure.db.edongfang.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.customer.first.biz.pojo.dto.EdongfangLogisticDTO;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangLogistics;
import thriving.softwood.customer.first.infrastructure.db.edongfang.mapper.base.EdongfangLogisticsMapper;
import thriving.softwood.customer.first.infrastructure.db.edongfang.mapper.extend.EdongfangLogisticsExtendMapper;

/**
 * <p>
 * 订单发货信息 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
@Service
public class EdongfangLogisticsRepo extends AncestorServiceImpl<EdongfangLogisticsMapper, EdongfangLogistics> {
    private final EdongfangLogisticsExtendMapper extendMapper;

    @Autowired
    public EdongfangLogisticsRepo(EdongfangLogisticsExtendMapper extendMapper) {
        this.extendMapper = extendMapper;
    }

    public List<EdongfangLogisticDTO> listUnnotified(String maxOrderId) {
        return extendMapper.listUnnotified(maxOrderId);
    }
}
