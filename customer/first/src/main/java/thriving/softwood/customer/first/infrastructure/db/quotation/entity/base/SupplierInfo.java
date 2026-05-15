package thriving.softwood.customer.first.infrastructure.db.quotation.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 供应商信息表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@Data
@NoArgsConstructor
@TableName("supplier_info")
public class SupplierInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键，唯一标识每条供应商信息记录
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 供应商编码
     */
    @TableField("supplier_code")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplierName;

    /**
     * 从此处向下为基础信息
     */
    @TableField("basic_info")
    private String basicInfo;

    /**
     * 地区(省-市-县)
     */
    @TableField("region")
    private String region;

    /**
     * 原材料名称
     */
    @TableField("raw_material_name")
    private String rawMaterialName;

    /**
     * 报价负责人
     */
    @TableField("quotation_manager")
    private String quotationManager;

    /**
     * 内部联系人
     */
    @TableField("internal_contact")
    private String internalContact;

    /**
     * 采购联系人
     */
    @TableField("purchase_contact")
    private String purchaseContact;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 联系人电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 通知方式(通知链接)
     */
    @TableField("notify_method")
    private String notifyMethod;

    /**
     * 附件
     */
    @TableField("attachment")
    private String attachment;

    /**
     * 从此处向下为财务信息
     */
    @TableField("financial_info")
    private String financialInfo;

    /**
     * 增值税税率
     */
    @TableField("vat_rate")
    private BigDecimal vatRate;

    /**
     * 开票名称
     */
    @TableField("invoice_name")
    private String invoiceName;

    /**
     * 开票税号
     */
    @TableField("tax_id")
    private String taxId;

    /**
     * 开户银行
     */
    @TableField("bank_name")
    private String bankName;

    /**
     * 银行账号
     */
    @TableField("bank_account")
    private String bankAccount;

    /**
     * 开户地址
     */
    @TableField("bank_address")
    private String bankAddress;

    /**
     * 开户电话
     */
    @TableField("bank_phone")
    private String bankPhone;

    /**
     * 关联的联系人
     */
    @TableField("related_contact")
    private String relatedContact;

    @TableField("deleted")
    private Integer deleted;

    @TableField("last_modifier")
    private String lastModifier;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 扩展信息
     */
    @TableField("ext_info")
    private String extInfo;
}
