package thriving.softwood.kaishi.biz.api.edongfang;

import static thriving.softwood.common.database.enums.DbOperationType.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.map.MapUtil;
import cn.hutool.v7.json.JSONUtil;
import thriving.softwood.common.security.context.UserContext;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangProductQryReq;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangProductReq;
import thriving.softwood.kaishi.biz.pojo.vo.EdongfangProductDetailVO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.*;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.*;

@Service
@DS("edongfang") // 强制使用 MySQL 数据源
public class EdongfangProductSvc implements EdongfangProductApi {

    private final EdongfangProductsRepo productsRepo;
    private final EdongfangProductPricesRepo pricesRepo;
    private final EdongfangProductImagesRepo imagesRepo;
    private final EdongfangProductParamsRepo paramsRepo;
    private final EdongfangProductStocksRepo stocksRepo;
    private final EdongfangMessagesRepo messagesRepo; // 消息同步表

    public EdongfangProductSvc(EdongfangProductsRepo productsRepo, EdongfangProductPricesRepo pricesRepo,
        EdongfangProductImagesRepo imagesRepo, EdongfangProductParamsRepo paramsRepo,
        EdongfangProductStocksRepo stocksRepo, EdongfangMessagesRepo messagesRepo) {
        this.productsRepo = productsRepo;
        this.pricesRepo = pricesRepo;
        this.imagesRepo = imagesRepo;
        this.paramsRepo = paramsRepo;
        this.stocksRepo = stocksRepo;
        this.messagesRepo = messagesRepo;
    }

    /**
     * 📊 1. 商品信息分页列表
     */
    @Override
    public Page<EdongfangProducts> pageProducts(EdongfangProductQryReq qryReq) {
        return productsRepo.pageProducts(qryReq);
    }

    /**
     * 🔍 2. 商品全景详情查询 根据 sku 一次性拼装主子表数据
     */
    @Override
    public EdongfangProductDetailVO getProductDetail(String sku) {
        EdongfangProducts product = productsRepo.lambdaQuery().eq(EdongfangProducts::getSku, sku).one();

        if (product == null) {
            throw new RuntimeException("商品不存在，SKU: " + sku);
        }

        // 串行查询 4 张子表 (因 MySQL 本地主键/索引命中，总耗时通常在 2-5ms 内，无需上多线程)
        var prices = pricesRepo.lambdaQuery().eq(EdongfangProductPrices::getSku, sku).list();

        // 注意：图片一般有展示顺序，这里按照 order 字段升序排列
        var images =
            imagesRepo.lambdaQuery().eq(EdongfangProductImages::getSku, sku).last(" order by `order` asc").list();

        var params = paramsRepo.lambdaQuery().eq(EdongfangProductParams::getSku, sku).list();
        var stocks = stocksRepo.lambdaQuery().eq(EdongfangProductStocks::getSku, sku).list();

        return new EdongfangProductDetailVO(product, prices, images, params, stocks);
    }

    /**
     * 🌟 1. 新增商品
     */
    @Override
    public void addProduct(EdongfangProductReq req) {
        EdongfangProducts product = req.product();
        String sku = product.getSku();
        product.setPk(IdUtil.fastSimpleUUID());
        productsRepo.save(product);

        saveRelatedData(sku, req);

        // 插入 E采平台同步消息 (新增: state=A)
        saveEdongfangMessage("205", MapUtil.ofKvs(false, "skuId", sku, "state", "A"));
    }

    /**
     * 🌟 2. 修改商品信息 (包含差异比对发消息)
     */
    @Override
    public void updateProduct(EdongfangProductReq req) {
        EdongfangProducts product = req.product();
        String sku = product.getSku();

        productsRepo.updateById(product);
        saveRelatedData(sku, req);

        boolean priceChanged = CollUtil.isNotEmpty(req.prices())
            && req.prices().stream().anyMatch(dto -> !SELECT.code().equals(dto.getDealType()));
        boolean stockChanged = CollUtil.isNotEmpty(req.stocks())
            && req.stocks().stream().anyMatch(dto -> !SELECT.code().equals(dto.getDealType()));
        // 插入 E采平台同步消息
        if (priceChanged) {
            saveEdongfangMessage("202", MapUtil.ofKvs(false, "skuId", sku));
        }
        if (stockChanged) {
            saveEdongfangMessage("203", MapUtil.ofKvs(false, "skuId", sku));
        }
        // 主信息修改通知 (修改: state=M)
        saveEdongfangMessage("205", MapUtil.ofKvs(false, "skuId", sku, "state", "M"));
    }

    /**
     * 🌟 3. 上下架状态修改
     */
    @Override
    public void changeState(List<String> skus, Integer state) {
        productsRepo.updateState(skus, state);

        // 批量发消息
        for (String sku : skus) {
            saveEdongfangMessage("204", MapUtil.ofKvs(false, "skuId", sku, "state", state));
        }
    }

    // --- 内部辅助方法 ---

    private void saveRelatedData(String sku, EdongfangProductReq req) {
        if (CollUtil.isNotEmpty(req.prices())) {
            savePrices(sku, req);
        }

        if (CollUtil.isNotEmpty(req.stocks())) {
            saveStocks(sku, req);
        }

        if (CollUtil.isNotEmpty(req.images())) {
            saveImages(sku, req);

        }

        if (CollUtil.isNotEmpty(req.params())) {
            saveParams(sku, req);
        }

    }

    private void saveParams(String sku, EdongfangProductReq req) {
        String loginAccount = UserContext.loginAccount();
        LocalDateTime now = LocalDateTime.now();
        req.params().stream().filter(dto -> DELETE.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setUpdateBy(loginAccount);
            dto.setUpdateTime(now);
            paramsRepo.updateById(dto);
            paramsRepo.removeById(dto);
        });
        req.params().stream().filter(dto -> UPDATE.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setUpdateBy(loginAccount);
            dto.setUpdateTime(now);
            paramsRepo.updateById(dto);
        });
        req.params().stream().filter(dto -> INSERT.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setPk(IdUtil.fastSimpleUUID());
            dto.setSku(sku);
            dto.setCreateBy(loginAccount);
            dto.setCreateTime(now);
            paramsRepo.save(dto);
        });
    }

    private void saveImages(String sku, EdongfangProductReq req) {
        String loginAccount = UserContext.loginAccount();
        LocalDateTime now = LocalDateTime.now();
        req.images().stream().filter(dto -> DELETE.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setUpdateBy(loginAccount);
            dto.setUpdateTime(now);
            imagesRepo.updateById(dto);
            imagesRepo.removeById(dto);
        });
        req.images().stream().filter(dto -> UPDATE.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setUpdateBy(loginAccount);
            dto.setUpdateTime(now);
            imagesRepo.updateById(dto);
        });
        req.images().stream().filter(dto -> INSERT.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setPk(IdUtil.fastSimpleUUID());
            dto.setSku(sku);
            dto.setCreateBy(loginAccount);
            dto.setCreateTime(now);
            imagesRepo.save(dto);
        });
    }

    private void saveStocks(String sku, EdongfangProductReq req) {
        String loginAccount = UserContext.loginAccount();
        LocalDateTime now = LocalDateTime.now();
        req.stocks().stream().filter(dto -> DELETE.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setUpdateBy(loginAccount);
            dto.setUpdateTime(now);
            stocksRepo.updateById(dto);
            stocksRepo.removeById(dto);
        });
        req.stocks().stream().filter(dto -> UPDATE.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setUpdateBy(loginAccount);
            dto.setUpdateTime(now);
            stocksRepo.updateById(dto);
        });
        req.stocks().stream().filter(dto -> INSERT.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setPk(IdUtil.fastSimpleUUID());
            dto.setSku(sku);
            dto.setCreateBy(loginAccount);
            dto.setCreateTime(now);
            stocksRepo.save(dto);
        });
    }

    private void savePrices(String sku, EdongfangProductReq req) {
        String loginAccount = UserContext.loginAccount();
        LocalDateTime now = LocalDateTime.now();
        req.prices().stream().filter(dto -> DELETE.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setUpdateBy(loginAccount);
            dto.setUpdateTime(now);
            pricesRepo.updateById(dto);
            pricesRepo.removeById(dto);
        });
        req.prices().stream().filter(dto -> UPDATE.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setUpdateBy(loginAccount);
            dto.setUpdateTime(now);
            pricesRepo.updateById(dto);
        });
        req.prices().stream().filter(dto -> INSERT.code().equals(dto.getDealType())).forEach(dto -> {
            dto.setPk(IdUtil.fastSimpleUUID());
            dto.setSku(sku);
            dto.setCreateBy(loginAccount);
            dto.setCreateTime(now);
            pricesRepo.save(dto);
        });
    }

    private void saveEdongfangMessage(String type, java.util.Map<String, Object> resultMap) {
        EdongfangMessages msg = new EdongfangMessages();
        msg.setPk(IdUtil.fastSimpleUUID());
        msg.setType(type);
        msg.setResult(JSONUtil.toJsonStr(resultMap));
        msg.setConsumed(0);
        msg.setDeleted(0);
        messagesRepo.save(msg);
    }
}