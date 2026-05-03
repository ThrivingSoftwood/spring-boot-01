package thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base;

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
 * 订单生命周期状态存根与通知判定凭据表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-03
 */
@Data
@NoArgsConstructor
@TableName("edongfang_order_stub")
public class EdongfangOrderStub implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * E采平台订单编号(业务核心关联键)
     */
    @TableField("e_order_id")
    private String eOrderId;

    /**
     * 上一次记录的订单处理状态(-1:取消, 0:未确认, 1:已确认)
     */
    @TableField("last_submit_state")
    private Integer lastSubmitState;

    /**
     * 上一次记录的订单状态(0:新建, 5:发货, -2:取消, 1:签收等)
     */
    @TableField("last_status")
    private Integer lastStatus;

    /**
     * 上一次记录的物流签收时间
     */
    @TableField("last_receive_time")
    private LocalDateTime lastReceiveTime;

    /**
     * 发货状态(0:未发货, 1:已发货)
     */
    @TableField("shipped_flag")
    private Integer shippedFlag;

    /**
     * 新预购订单消息是否已发送(0:未发, 1:已发)
     */
    @TableField("preorder_notified")
    private Boolean preorderNotified;

    /**
     * 磋商结果(确认/取消)通知是否已发送(0:未发, 1:已发)
     */
    @TableField("consult_result_notified")
    private Boolean consultResultNotified;

    /**
     * 客户确认收货通知是否已发送(0:未发, 1:已发)
     */
    @TableField("confirm_receipt_notified")
    private Boolean confirmReceiptNotified;

    /**
     * 订单金额
     */
    @TableField("order_price")
    private BigDecimal orderPrice;

    /**
     * 采购人
     */
    @TableField("purchaser")
    private String purchaser;

    /**
     * 收货人
     */
    @TableField("name")
    private String name;

    /**
     * 存根创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 最后一次同步更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
