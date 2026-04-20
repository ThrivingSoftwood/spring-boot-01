package thriving.softwood.common.auth.infrastructure.db.master.entity.base;

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
 * @author meta-thriving
 * @since 2026-04-17
 */
@Data
@NoArgsConstructor
@TableName("sys_user")
public class SysUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("login_account")
    private String loginAccount;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("dept_id")
    private Long deptId;

    @TableField("permission_version")
    private String permissionVersion;

    @TableField("status")
    private Byte status;

    @TableField("login_time")
    private String loginTime;

    @TableField("last_password")
    private String lastPassword;

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
