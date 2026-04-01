package thriving.softwood.common.database.ancestor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 📘 通用实体基类 业务实体只需继承此类，即可自动获得时间戳与逻辑删除管理。
 */
@Data
public abstract class AncestorDbEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 自增 id
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 最后修改人
    @TableField("lastModifier")
    private String lastModifier;

    // 最后修改人
    @TableField("extInfo")
    private String extInfo;

    // 自动填充：插入时生效
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 自动填充：插入和更新时生效
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 逻辑删除标志 (0: 存在, 1: 已删除)
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}