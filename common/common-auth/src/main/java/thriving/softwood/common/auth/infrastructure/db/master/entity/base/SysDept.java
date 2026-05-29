package thriving.softwood.common.auth.infrastructure.db.master.entity.base;

import static thriving.softwood.common.auth.constant.BaseConst.ROOT_DEPARTMENT_ID_STR;
import static thriving.softwood.common.auth.constant.BaseConst.ROOT_PARENT_DEPT_ID_LONG;
import static thriving.softwood.common.core.constant.PunctuationConstant.COMMA;

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
 * @since 2026-04-17
 */
@Data
@NoArgsConstructor
@TableName("sys_dept")
public class SysDept implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;
    @TableField("parent_id")
    protected Long parentId;
    @TableField("ancestors")
    protected String ancestors;
    @TableField("dept_name")
    protected String deptName;
    @TableField("sort_order")
    protected Integer sortOrder;
    @TableField("status")
    protected Byte status;
    @TableField("ext_info")
    protected String extInfo;
    @TableField("deleted")
    protected Integer deleted;
    @TableField("last_modifier")
    protected String lastModifier;
    @TableField("create_time")
    protected LocalDateTime createTime;
    @TableField("update_time")
    protected LocalDateTime updateTime;

    public SysDept(SysDept parentDept, String deptName, Integer sortOrder) {
        parentId = ROOT_PARENT_DEPT_ID_LONG;
        ancestors = ROOT_DEPARTMENT_ID_STR;
        if (null != parentDept) {
            parentId = parentDept.getId();
            ancestors = parentDept.getAncestors() + COMMA + parentDept.getId();
        }
        this.deptName = deptName;
        this.sortOrder = sortOrder;
        status = (byte)1;
        deleted = 0;
    }
}
