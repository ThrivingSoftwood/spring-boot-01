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
 * 订单发货商品表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@TableName("edongfang_logistics_items")
public class EdongfangLogisticsItems implements Serializable {

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
    @TableField("e_order_id")
    private String eOrderId;

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
     * 发货的pk
     */
    @TableField("parent")
    private String parent;

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
}
