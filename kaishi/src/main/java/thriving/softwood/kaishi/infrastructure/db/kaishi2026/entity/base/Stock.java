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
@TableName("Stock")
public class Stock implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("typeId")
    private String typeId;

    @TableField("parid")
    private String parid;

    @TableField("leveal")
    private Short leveal;

    @TableField("sonnum")
    private Integer sonnum;

    @TableField("soncount")
    private Integer soncount;

    @TableField("FullName")
    private String fullName;

    @TableField("Name")
    private String name;

    @TableField("UserCode")
    private String userCode;

    @TableField("Comment")
    private String comment;

    @TableField("deleted")
    private Boolean deleted;

    @TableField("Namepy")
    private String namepy;

    @TableField("Rec")
    private Integer rec;

    @TableField("ParRec")
    private Integer parRec;

    @TableField("OrderNo")
    private Integer orderNo;

    @TableField("UpdateTag")
    private Integer updateTag;

    @TableField("bPosition")
    private Byte bPosition;

    @TableField("AllowNegative")
    private Boolean allowNegative;

    @TableField("CreateDate")
    private LocalDateTime createDate;
}
