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
 * 商品信息表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@Data
@NoArgsConstructor
@TableName("product_info")
public class ProductInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键，唯一标识每条商品信息记录
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商品编码
     */
    @TableField("product_code")
    private String productCode;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 规格型号
     */
    @TableField("specification")
    private String specification;

    /**
     * 标准采购价(单位:元)
     */
    @TableField("purchase_price")
    private BigDecimal purchasePrice;

    /**
     * 标准销售价(含税,单位:元)
     */
    @TableField("sale_price")
    private BigDecimal salePrice;

    /**
     * 税率(%)
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 默认供应商
     */
    @TableField("default_supplier")
    private String defaultSupplier;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 可选供应商
     */
    @TableField("optional_suppliers")
    private String optionalSuppliers;

    /**
     * 数据状态
     */
    @TableField("data_status")
    private String dataStatus;

    /**
     * 禁用状态
     */
    @TableField("disable_status")
    private String disableStatus;

    /**
     * 物料属性
     */
    @TableField("material_property")
    private String materialProperty;

    /**
     * 基本单位
     */
    @TableField("base_unit")
    private String baseUnit;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 附件
     */
    @TableField("attachment")
    private String attachment;

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
