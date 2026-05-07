package thriving.softwood.kaishi.infrastructure.db.edongfang.repo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.biz.pojo.dto.EdongfangOrderDTO;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangOrderQryReq;
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

    public EdongfangOrders getByOrderId(String eOrderId) {
        return lambdaQuery().eq(EdongfangOrders::getEOrderId, eOrderId).one();
    }

    public Page<EdongfangOrderDTO> page(long pageNo, long pageSize, EdongfangOrderQryReq req) {
        LambdaQueryWrapper<EdongfangOrderDTO> wrapper = Wrappers.lambdaQuery();

        // 🌟 支持逗号分隔的 E采平台订单号 IN 查询
        if (StrUtil.isNotBlank(req.eOrderIds())) {
            List<String> idList = Arrays.asList(req.eOrderIds().split(","));
            wrapper.in(EdongfangOrderDTO::getEOrderId, idList);
        }

        // 🌟 需求 2.3.9：发货信息页面复用（增加条件 status not in ('0','-2')）
        if (Boolean.FALSE.equals(req.queryShipped()) && null != req.status()) {
            wrapper.eq(EdongfangOrders::getStatus, req.status());
        }

        wrapper.orderByDesc(EdongfangOrders::getCreateTime);
        return extendMapper.page(new Page<>(pageNo, pageSize), req.queryShipped(), wrapper);
    }
}
