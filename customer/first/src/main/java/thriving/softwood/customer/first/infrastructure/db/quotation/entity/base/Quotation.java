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
 * 报价单信息表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@Data
@NoArgsConstructor
@TableName("quotation")
public class Quotation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键，唯一标识每条报价记录
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 报价单号
     */
    @TableField("quotation_no")
    private String quotationNo;

    /**
     * 所属供应商编码
     */
    @TableField("supplier_code")
    private String supplierCode;

    /**
     * 商品信息
     */
    @TableField("product_info")
    private String productInfo;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品编码
     */
    @TableField("product_code")
    private String productCode;

    /**
     * 供货地
     */
    @TableField("supply_place")
    private String supplyPlace;

    /**
     * 可供货数量
     */
    @TableField("available_qty")
    private BigDecimal availableQty;

    /**
     * 数量单位
     */
    @TableField("quantity_unit")
    private String quantityUnit;

    /**
     * 报价类型
     */
    @TableField("quotation_type")
    private String quotationType;

    /**
     * 报价单价(含税)
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 增值税税率(%)
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 报价备注信息
     */
    @TableField("remark")
    private String remark;

    /**
     * 报价辅助材料
     */
    @TableField("auxiliary_material")
    private String auxiliaryMaterial;

    /**
     * 过期(失效)标记 (0:生效, 1:失效)
     */
    @TableField("outdated")
    private Integer outdated;

    /**
     * 已删除标记 (0:正常, 1:删除)
     */
    @TableField("deleted")
    private Integer deleted;

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
