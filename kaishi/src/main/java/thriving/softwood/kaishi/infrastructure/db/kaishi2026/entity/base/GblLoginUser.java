package thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("T_GBL_LoginUser")
public class GblLoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("etypeid")
    private String etypeid;

    @TableField("password")
    private byte[] password;

    @TableField("sysid")
    private String sysid;

    @TableField("limit")
    private String limit;

    @TableField("ismanager")
    private Integer ismanager;

    @TableField("Pass_Code")
    private String passCode;

    @TableField("Pass_Level")
    private Short passLevel;

    @TableField("Pass_Have")
    private Short passHave;

    @TableField("SendID")
    private String sendID;

    @TableField("ClientId")
    private String clientId;

    @TableField("UserCode")
    private String userCode;

    @TableField("FullName")
    private String fullName;

    @TableField("IsSpecial")
    private Byte isSpecial;

    @TableField("DtypeId")
    private String dtypeId;

    @TableField("Rec")
    private Long rec;

    @TableField("IsPhone")
    private Integer isPhone;

    @TableField("UseErp")
    private Integer useErp;

    @TableField("UseFactory")
    private Integer useFactory;

    @TableField("LastChangePass")
    private String lastChangePass;
}
