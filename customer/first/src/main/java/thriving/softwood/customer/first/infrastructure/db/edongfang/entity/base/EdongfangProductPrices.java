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
 * 商品价格表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@TableName("edongfang_product_prices")
public class EdongfangProductPrices implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("pk")
    private String pk;

    /**
     * 商品编号
     */
    @TableField("sku")
    private String sku;

    /**
     * 市场售价
     */
    @TableField("market_price")
    private BigDecimal marketPrice;

    /**
     * 商城售价
     */
    @TableField("mall_price")
    private BigDecimal mallPrice;

    /**
     * 协议优惠价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 商品税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 商品裸价
     */
    @TableField("naked_price")
    private BigDecimal nakedPrice;

    /**
     * 发票税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

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
