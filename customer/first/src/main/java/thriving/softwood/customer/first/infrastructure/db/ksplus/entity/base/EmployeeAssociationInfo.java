package thriving.softwood.customer.first.infrastructure.db.ksplus.entity.base;

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
@TableName("employee_association_info")
public class EmployeeAssociationInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 注意,这个列不是从 T_GBL_LOGINUSER 表来的数据 */
    @TableField("login_account")
    private String loginAccount;

    @TableField("ori_fullname")
    private String oriFullname;

    @TableField("employee_typeid")
    private String employeeTypeid;

    @TableField("employee_user_code")
    private String employeeUserCode;

    @TableField("login_employee_typeid")
    private String loginEmployeeTypeid;

    @TableField("login_user_code")
    private String loginUserCode;

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
