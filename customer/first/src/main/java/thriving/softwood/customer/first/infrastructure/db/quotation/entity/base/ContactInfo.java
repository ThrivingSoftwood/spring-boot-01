package thriving.softwood.customer.first.infrastructure.db.quotation.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 联系人表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@Data
@NoArgsConstructor
@TableName("contact_info")
public class ContactInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键，唯一标识每条联系人信息记录
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    @TableField("name")
    private String name;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 主要联系人标记
     */
    @TableField("is_main_contact")
    private String isMainContact;

    /**
     * 关联的客户
     */
    @TableField("related_customer")
    private String relatedCustomer;

    /**
     * 关联的供应商
     */
    @TableField("related_supplier")
    private String relatedSupplier;

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
