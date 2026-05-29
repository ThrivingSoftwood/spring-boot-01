package thriving.softwood.customer.first.infrastructure.db.master.entity.base;

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
 * 系统字典表
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@Data
@NoArgsConstructor
@TableName("sys_dictionary")
public class SysDictionary implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 字典类型
     */
    @TableField("dict_type")
    private String dictType;

    /**
     * 字典键
     */
    @TableField("dict_key")
    private String dictKey;

    /**
     * 字典值
     */
    @TableField("dict_value")
    private String dictValue;

    /**
     * 排序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态 (1:启用, 0:禁用)
     */
    @TableField("status")
    private Short status;

    /**
     * 备注说明
     */
    @TableField("remark")
    private String remark;

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
