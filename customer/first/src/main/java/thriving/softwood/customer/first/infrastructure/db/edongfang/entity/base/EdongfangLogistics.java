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
 * 订单发货信息
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@TableName("edongfang_logistics")
public class EdongfangLogistics implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("pk")
    private String pk;

    /**
     * E采平台订单编号
     */
    @TableField("e_order_id")
    private String eOrderId;

    /**
     * 订单物流状态 0：新建-1： 拒收-2 ：已取消 1：妥投完成 4： 退换货中 5 ：已出库
     */
    @TableField("logistics_state")
    private Integer logisticsState;

    /**
     * 订单处理状态-1：取消 0 ：未确认 1：已确认
     */
    @TableField("submit_state")
    private Integer submitState;

    /**
     * 发货单id（若无拆单，为e_order_id值）
     */
    @TableField("package_id")
    private String packageId;

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
     * 订单金额
     */
    @TableField("order_price")
    private BigDecimal orderPrice;

    /**
     * 订单类型 1：母订单 2：子订单
     */
    @TableField("order_type")
    private Integer orderType;

    /**
     * 物流公司
     */
    @TableField("express_company")
    private String expressCompany;

    /**
     * 物流号
     */
    @TableField("express_no")
    private String expressNo;

    /**
     * 发票号码
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 发票编码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 0 订单物流 1 发票物流
     */
    @TableField("type")
    private Integer type;

    /**
     * 签收时间
     */
    @TableField("receive_time")
    private LocalDateTime receiveTime;
}
