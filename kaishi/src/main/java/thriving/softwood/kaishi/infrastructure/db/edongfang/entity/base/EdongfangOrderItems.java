package thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base;

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
 * 订单商品表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@TableName("edongfang_order_items")
public class EdongfangOrderItems implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("pk")
    private String pk;

    /**
     * 订单表外键
     */
    @TableField("parent")
    private String parent;

    /**
     * 商品编号
     */
    @TableField("sku")
    private String sku;

    /**
     * 商品数量
     */
    @TableField("num")
    private BigDecimal num;

    /**
     * 含税单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 未税单价
     */
    @TableField("naked_price")
    private BigDecimal nakedPrice;

    /**
     * 商品税额
     */
    @TableField("tax_price")
    private BigDecimal taxPrice;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 商品未税总额
     */
    @TableField("naked_price_total")
    private BigDecimal nakedPriceTotal;

    /**
     * 商品税额合计
     */
    @TableField("tax_price_total")
    private BigDecimal taxPriceTotal;

    /**
     * 商品含税总价/商品小计
     */
    @TableField("price_total")
    private BigDecimal priceTotal;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 客户物料名称
     */
    @TableField("material_name")
    private String materialName;

    /**
     * 客户物料编码
     */
    @TableField("material_code")
    private String materialCode;

    /**
     * 商品备注
     */
    @TableField("description")
    private String description;

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

    /**
     * 签收数量
     */
    @TableField("signed_count")
    private BigDecimal signedCount;

    /**
     * 签收金额
     */
    @TableField("signed_amount")
    private BigDecimal signedAmount;

    /**
     * 退货数量
     */
    @TableField("return_count")
    private BigDecimal returnCount;

    /**
     * 退股金额
     */
    @TableField("return_amount")
    private BigDecimal returnAmount;

    /**
     * E采平台订单编号
     */
    @TableField("e_order_id")
    private String eOrderId;
}
