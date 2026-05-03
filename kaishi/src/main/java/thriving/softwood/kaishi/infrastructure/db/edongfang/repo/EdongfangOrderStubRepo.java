package thriving.softwood.kaishi.infrastructure.db.edongfang.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import cn.hutool.v7.core.collection.CollUtil;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrderStub;
import thriving.softwood.kaishi.infrastructure.db.edongfang.mapper.base.EdongfangOrderStubMapper;

/**
 * <p>
 * 订单生命周期状态存根与通知判定凭据表 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-29
 */
@DS("edongfang")
@Service
public class EdongfangOrderStubRepo extends AncestorServiceImpl<EdongfangOrderStubMapper, EdongfangOrderStub> {

    public void shipOrders(List<String> eOrderIds) {
        if (CollUtil.isEmpty(eOrderIds)) {
            return;
        }
        lambdaUpdate().in(EdongfangOrderStub::getEOrderId, eOrderIds).set(EdongfangOrderStub::getShippedFlag, 1)
            .update();
    }

    public EdongfangOrderStub getByOrderId(String eOrderId) {
        return lambdaQuery().eq(EdongfangOrderStub::getEOrderId, eOrderId).one();
    }

}
