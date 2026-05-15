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
 * 开票申请表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@TableName("edongfang_invoices")
public class EdongfangInvoices implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("pk")
    private String pk;

    /**
     * 结算单号
     */
    @TableField("bill_no")
    private String billNo;

    /**
     * 发票请求子订单号
     */
    @TableField("e_order_ids")
    private String eOrderIds;

    /**
     * 第三方申请发票的唯一id标识
     */
    @TableField("mark_id")
    private String markId;

    /**
     * 结算单子订单总数
     */
    @TableField("settle_num")
    private Integer settleNum;

    /**
     * 结算单不含税总金额（裸价）
     */
    @TableField("settle_naked_price")
    private BigDecimal settleNakedPrice;

    /**
     * 结算单总税价
     */
    @TableField("settle_tax_price")
    private BigDecimal settleTaxPrice;

    /**
     * 发票类型
     */
    @TableField("invoice_type")
    private Integer invoiceType;

    /**
     * 开票内容
     */
    @TableField("invoice_content")
    private String invoiceContent;

    /**
     * 期望开票时间
     */
    @TableField("invoice_expect_date")
    private String invoiceExpectDate;

    /**
     * 发票抬头
     */
    @TableField("invoice_title")
    private String invoiceTitle;

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
     * 税号
     */
    @TableField("invoice_tax_num")
    private String invoiceTaxNum;

    /**
     * 发票开户行
     */
    @TableField("invoice_bank")
    private String invoiceBank;

    /**
     * 银行账号
     */
    @TableField("invoice_bank_accout")
    private String invoiceBankAccout;

    /**
     * 收票单位
     */
    @TableField("invoice_company_name")
    private String invoiceCompanyName;

    /**
     * 收票人
     */
    @TableField("bill_toer")
    private String billToer;

    /**
     * 收票人联系方式
     */
    @TableField("bill_to_contact")
    private String billToContact;

    /**
     * 收票人地址（省编码）
     */
    @TableField("bill_to_province")
    private String billToProvince;

    /**
     * 收票人地址（市编码）
     */
    @TableField("bill_to_city")
    private String billToCity;

    /**
     * 收票人地址（区编码）
     */
    @TableField("bill_to_county")
    private String billToCounty;

    /**
     * 收票人地址（镇编码）
     */
    @TableField("bill_to_town")
    private String billToTown;

    /**
     * 收票人全量地址
     */
    @TableField("bill_to_address")
    private String billToAddress;

    /**
     * 预计还款时间
     */
    @TableField("repayment_date")
    private String repaymentDate;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 发票号码
     */
    @TableField("invoice_num")
    private String invoiceNum;

    /**
     * 发票金额（裸价）
     */
    @TableField("invoice_naked_amount")
    private BigDecimal invoiceNakedAmount;

    /**
     * 发票税率
     */
    @TableField("invoice_tax_rate")
    private BigDecimal invoiceTaxRate;

    /**
     * 发票税额
     */
    @TableField("invoice_tax_amount")
    private BigDecimal invoiceTaxAmount;

    /**
     * 价税合计
     */
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;
}
