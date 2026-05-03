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
 * @since 2026-04-16
 */
@Data
@NoArgsConstructor
@TableName("department_association_info")
public class DepartmentAssociationInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("auth_department_id")
    private Long authDepartmentId;
    @TableField("ori_department_typeid")
    private String oriDepartmentTypeid;
    @TableField("ext_info")
    private String extInfo;
    @TableField("deleted")
    private Integer deleted;
    @TableField("last_modifier")
    private String lastModifier;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;

    public DepartmentAssociationInfo(Long authDepartmentId, String oriDepartmentTypeid) {
        this.authDepartmentId = authDepartmentId;
        this.oriDepartmentTypeid = oriDepartmentTypeid;
        deleted = 0;
    }
}
