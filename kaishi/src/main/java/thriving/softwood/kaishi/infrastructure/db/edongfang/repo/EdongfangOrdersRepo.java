package thriving.softwood.kaishi.infrastructure.db.edongfang.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.biz.pojo.dto.EdongfangOrderDTO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrders;
import thriving.softwood.kaishi.infrastructure.db.edongfang.mapper.base.EdongfangOrdersMapper;
import thriving.softwood.kaishi.infrastructure.db.edongfang.mapper.extend.EdongfangOrdersExtendMapper;

/**
 * <p>
 * 订单创建主表 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
@Service
public class EdongfangOrdersRepo extends AncestorServiceImpl<EdongfangOrdersMapper, EdongfangOrders> {
    private final EdongfangOrdersExtendMapper extendMapper;

    @Autowired
    public EdongfangOrdersRepo(EdongfangOrdersExtendMapper extendMapper) {
        this.extendMapper = extendMapper;
    }

    public String getMaxOrderId() {
        return lambdaQuery().orderByDesc(EdongfangOrders::getEOrderId).last(" limit 1").one().getEOrderId();
    }

    public List<EdongfangOrderDTO> listUnnotified(String maxOrderId) {
        return extendMapper.listUnnotified(maxOrderId);
    }
}
