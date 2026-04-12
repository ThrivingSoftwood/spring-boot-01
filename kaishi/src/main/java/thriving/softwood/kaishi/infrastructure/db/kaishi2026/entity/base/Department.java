package thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-06
 */
@Data
@NoArgsConstructor
@TableName("Department")
public class Department implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("typeid")
    private String typeid;

    @TableField("parid")
    private String parid;

    @TableField("leveal")
    private Integer leveal;

    @TableField("sonnum")
    private Integer sonnum;

    @TableField("soncount")
    private Integer soncount;

    @TableField("usercode")
    private String usercode;

    @TableField("FullName")
    private String fullName;

    @TableField("Comment")
    private String comment;

    @TableField("deleted")
    private Boolean deleted;

    @TableField("rec")
    private Integer rec;

    @TableField("namepy")
    private String namepy;

    @TableField("parrec")
    private Integer parrec;

    @TableField("OrderNo")
    private Integer orderNo;

    @TableField("deptype")
    private Integer deptype;

    @TableField("Costtype")
    private Integer costtype;

    @TableField("Atypeid")
    private String atypeid;

    @TableField("UpdateTag")
    private Integer updateTag;

    @TableField("EtypeID")
    private String etypeID;

    @TableField("CreateDate")
    private LocalDateTime createDate;
}
