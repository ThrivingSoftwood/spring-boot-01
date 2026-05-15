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
 * 订单创建主表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@TableName("edongfang_orders")
public class EdongfangOrders implements Serializable {

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
     * 收货人
     */
    @TableField("name")
    private String name;

    /**
     * 一级地址
     */
    @TableField("province")
    private Integer province;

    /**
     * 一级地址名称
     */
    @TableField("province_name")
    private String provinceName;

    /**
     * 二级地址
     */
    @TableField("city")
    private Integer city;

    /**
     * 二级地址名称
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 三级地址
     */
    @TableField("county")
    private Integer county;

    /**
     * 三级地址名称
     */
    @TableField("county_name")
    private String countyName;

    /**
     * 采购人
     */
    @TableField("purchaser")
    private String purchaser;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 邮编
     */
    @TableField("zip")
    private String zip;

    /**
     * 座机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 手机号
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 订单备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 发票抬头
     */
    @TableField("invoice_title")
    private String invoiceTitle;

    /**
     * 发票类型
     */
    @TableField("invoice_type")
    private Integer invoiceType;

    /**
     * 发票税号
     */
    @TableField("invoice_tax_num")
    private String invoiceTaxNum;

    /**
     * 发票开户行
     */
    @TableField("invoice_bank")
    private String invoiceBank;

    /**
     * 发票银行账号
     */
    @TableField("invoice_bank_account")
    private String invoiceBankAccount;

    /**
     * 发票地址
     */
    @TableField("invoice_address")
    private String invoiceAddress;

    /**
     * 发票电话
     */
    @TableField("invoice_phone")
    private String invoicePhone;

    /**
     * 支付方式
     */
    @TableField("payment")
    private Integer payment;

    /**
     * 订单金额
     */
    @TableField("order_price")
    private BigDecimal orderPrice;

    /**
     * 运费
     */
    @TableField("freight")
    private BigDecimal freight;

    /**
     * 采购单位名称
     */
    @TableField("dep_name")
    private String depName;

    /**
     * 下单人电话
     */
    @TableField("purchaser_phone")
    private String purchaserPhone;

    /**
     * 下单人手机号
     */
    @TableField("purchaser_mobile")
    private String purchaserMobile;

    /**
     * 下单人邮箱
     */
    @TableField("purchaser_email")
    private String purchaserEmail;

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
     * 订单处理状态-1：取消 0 ：未确认 1：已确认
     */
    @TableField("submit_state")
    private Integer submitState;

    /**
     * 退换货状态 0：无退换货操作 1：退换货中 2：已完成退换货
     */
    @TableField("refund_status")
    private Integer refundStatus;

    /**
     * 订单状态 0：新建5：发货-2：取消-1：拒收1：签收4：退换货中
     */
    @TableField("status")
    private Integer status;
}
