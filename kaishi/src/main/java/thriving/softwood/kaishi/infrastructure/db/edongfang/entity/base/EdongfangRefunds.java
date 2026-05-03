package thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 退换货申请主表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@TableName("edongfang_refunds")
public class EdongfangRefunds implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("pk")
    private String pk;

    /**
     * 退换货申请编号
     */
    @TableField("apply_code")
    private String applyCode;

    /**
     * 退货对应发货单号
     */
    @TableField("package_id")
    private String packageId;

    /**
     * 退换货申请类型
     */
    @TableField("apply_type")
    private String applyType;

    /**
     * 退换货申请时间
     */
    @TableField("apply_time")
    private String applyTime;

    /**
     * 退换货原因
     */
    @TableField("apply_reason")
    private String applyReason;

    /**
     * 上门取件、第三方物流
     */
    @TableField("pickup_way")
    private String pickupWay;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 退换货联系人
     */
    @TableField("apply_name")
    private String applyName;

    /**
     * 退换货联系手机
     */
    @TableField("apply_mobile")
    private String applyMobile;

    /**
     * 退货人省份编码
     */
    @TableField("province_code")
    private String provinceCode;

    /**
     * 退货人城市编码
     */
    @TableField("city_code")
    private String cityCode;

    /**
     * 退货人区县编码
     */
    @TableField("county_code")
    private String countyCode;

    /**
     * 退货人详细地址
     */
    @TableField("full_address")
    private String fullAddress;

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
     * 操作人
     */
    @TableField("operator")
    private String operator;
}
