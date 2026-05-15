package thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@TableName("edongfang_products")
public class EdongfangProducts implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId("pk")
    private String pk;

    /**
     * 商品编号
     */
    @TableField("sku")
    private String sku;

    /**
     * 商品url
     */
    @TableField("url")
    private String url;

    /**
     * 型号
     */
    @TableField("model")
    private String model;

    /**
     * 重量
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 主图地址
     */
    @TableField("image_path")
    private String imagePath;

    /**
     * 1上架 0下架
     */
    @TableField("state")
    private Integer state;

    /**
     * 品牌
     */
    @TableField("brand_name")
    private String brandName;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 产地
     */
    @TableField("product_area")
    private String productArea;

    /**
     * 条形码
     */
    @TableField("upc")
    private String upc;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 分类
     */
    @TableField("category")
    private String category;

    /**
     * 分类名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 售后服务
     */
    @TableField("service")
    private String service;

    /**
     * 商品描述（图文, html）
     */
    @TableField("introduction")
    private String introduction;

    /**
     * 商品属性（html）
     */
    @TableField("param")
    private String param;

    /**
     * 包装清单
     */
    @TableField("ware")
    private String ware;

    /**
     * 商品税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 税收分类编码
     */
    @TableField("tax_category_code")
    private String taxCategoryCode;

    /**
     * 搜索关键词
     */
    @TableField("search_keyword")
    private String searchKeyword;

    /**
     * 是否促销
     */
    @TableField("sale_actives")
    private Byte saleActives;

    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Integer deleted;

    /**
     * 租户id
     */
    @TableField("tenant_id")
    private String tenantId;

    /**
     * 组织id
     */
    @TableField("cu")
    private String cu;
}
