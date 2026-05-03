package thriving.softwood.kaishi.infrastructure.db.edongfang.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangProductQryReq;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangProducts;
import thriving.softwood.kaishi.infrastructure.db.edongfang.mapper.base.EdongfangProductsMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
@Service
public class EdongfangProductsRepo extends AncestorServiceImpl<EdongfangProductsMapper, EdongfangProducts> {
    public void updateState(List<String> skus, Integer state) {
        lambdaUpdate().in(EdongfangProducts::getSku, skus).set(EdongfangProducts::getState, state).update();
    }

    public Page<EdongfangProducts> pageProducts(EdongfangProductQryReq qryReq) {
        Page<EdongfangProducts> page = new Page<>(qryReq.pageNo(), qryReq.pageSize());
        if (qryReq == null) {
            return lambdaQuery().orderByDesc(EdongfangProducts::getCreateTime).page(page);
        }
        return lambdaQuery()
            // 支持按 SKU 精准查询
            .eq(StrUtil.isNotBlank(qryReq.sku()), EdongfangProducts::getSku, qryReq.sku())
            // 支持按名称模糊查询
            .like(StrUtil.isNotBlank(qryReq.name()), EdongfangProducts::getName, qryReq.name())
            .orderByDesc(EdongfangProducts::getCreateTime).page(page);
    }

    public String getProductNameBySku(String sku) {
        return lambdaQuery().eq(EdongfangProducts::getSku, sku).one().getName();
    }
}
