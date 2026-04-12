package thriving.softwood.common.database.ancestor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;

/**
 * 📘 通用实体基类 业务实体只需继承此类，即可自动获得时间戳与逻辑删除管理。
 */
public abstract class AncestorDbEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 自增 id
    @TableId(value = "id", type = IdType.AUTO)
    protected Integer id;

    // 最后修改人
    @TableField("last_modifier")
    protected String lastModifier;

    // 最后修改人
    @TableField("ext_info")
    protected String extInfo;

    // 逻辑删除标志 (0: 存在, 1: 已删除)
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    protected Integer deleted;

    // 自动填充：插入时生效
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    // 自动填充：插入和更新时生效
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;
}