package thriving.softwood.kaishi.biz.api.edongfang;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import thriving.softwood.kaishi.biz.pojo.record.EdongfangProductQryReq;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangProductReq;
import thriving.softwood.kaishi.biz.pojo.vo.EdongfangProductDetailVO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangProducts;

public interface EdongfangProductApi {

    /**
     * 🌟 1. 新增商品
     */
    void addProduct(EdongfangProductReq req);

    /**
     * 🌟 2. 修改商品信息 (包含差异比对发消息)
     */
    void updateProduct(EdongfangProductReq req);

    /**
     * 🌟 3. 上下架状态修改
     */
    void changeState(List<String> skus, Integer state);

    Page<EdongfangProducts> pageProducts(EdongfangProductQryReq qryReq);

    /**
     * 🔍 2. 商品全景详情查询 根据 sku 一次性拼装主子表数据
     */
    EdongfangProductDetailVO getProductDetail(String sku);
}