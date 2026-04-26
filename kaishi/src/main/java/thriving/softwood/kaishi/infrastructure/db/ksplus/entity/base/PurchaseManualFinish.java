package thriving.softwood.kaishi.infrastructure.db.ksplus.entity.base;

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
 * 
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-08
 */
@Data
@NoArgsConstructor
@TableName("purchase_manual_finish")
public class PurchaseManualFinish implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("vch_type")
    private Integer vchType;

    @TableField("vch_code")
    private Long vchCode;

    @TableField("dly_order")
    private Long dlyOrder;

    @TableField("last_modifier")
    private String lastModifier;

    @TableField("ext_info")
    private String extInfo;

    @TableField("deleted")
    private Integer deleted;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
