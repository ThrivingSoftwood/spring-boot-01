package thriving.softwood.kaishi.infrastructure.db.master.entity.base;

import static thriving.softwood.common.core.constant.PunctuationConstant.COMMA;
import static thriving.softwood.kaishi.biz.constant.BaseConst.ROOT_DEPARTMENT_ID_STR;
import static thriving.softwood.kaishi.biz.constant.BaseConst.ROOT_PARENT_DEPT_ID_LONG;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Department;

/**
 * <p>
 * 
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-17
 */
@Data
@NoArgsConstructor
@TableName("sys_dept")
public class SysDept implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public SysDept(SysDept parentDept, Department department) {
        parentId = ROOT_PARENT_DEPT_ID_LONG;
        ancestors = ROOT_DEPARTMENT_ID_STR;
        if (null != parentDept) {
            parentId = parentDept.getId();
            ancestors = parentDept.getAncestors() + COMMA + parentDept.getId();
        }
        deptName = department.getFullName();
        sortOrder = Integer.valueOf(department.getTypeid());
        status = (byte)1;
        deleted = 0;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("parent_id")
    private Long parentId;

    @TableField("ancestors")
    private String ancestors;

    @TableField("dept_name")
    private String deptName;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("status")
    private Byte status;

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
}
