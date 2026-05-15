package thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base;

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
 * 消息
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@TableName("edongfang_messages")
public class EdongfangMessages implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("pk")
    private String pk;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 结果
     */
    @TableField("result")
    private String result;

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
     * 是否消费
     */
    @TableField("consumed")
    private Integer consumed;
}
