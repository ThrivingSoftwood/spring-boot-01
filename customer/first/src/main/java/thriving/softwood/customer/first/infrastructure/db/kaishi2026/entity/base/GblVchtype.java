package thriving.softwood.customer.first.infrastructure.db.kaishi2026.entity.base;

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
 * @since 2026-04-06
 */
@Data
@NoArgsConstructor
@TableName("T_GBL_Vchtype")
public class GblVchtype implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("vchtype")
    private Integer vchtype;

    @TableField("name")
    private String name;

    @TableField("fullname")
    private String fullname;

    @TableField("comment")
    private String comment;

    @TableField("typeid")
    private String typeid;

    @TableField("parid")
    private String parid;

    @TableField("leveal")
    private Short leveal;

    @TableField("sonnum")
    private Integer sonnum;

    @TableField("soncount")
    private Integer soncount;

    @TableField("deleted")
    private Boolean deleted;

    @TableField("version")
    private String version;

    @TableField("numberhead")
    private String numberhead;

    @TableField("inputn")
    private Integer inputn;

    @TableField("inputname1")
    private String inputname1;

    @TableField("inputname2")
    private String inputname2;

    @TableField("inputname3")
    private String inputname3;

    @TableField("inputname4")
    private String inputname4;

    @TableField("inputname5")
    private String inputname5;

    @TableField("SNMax")
    private Integer sNMax;

    @TableField("SNFormat")
    private String sNFormat;

    @TableField("classno")
    private Byte classno;

    @TableField("AuditingOrder")
    private Integer auditingOrder;

    @TableField("AuditingType")
    private Byte auditingType;

    @TableField("Summary")
    private String summary;

    @TableField("stringno")
    private Integer stringno;

    @TableField("dataareas")
    private String dataareas;

    @TableField("maingridtype")
    private Byte maingridtype;

    @TableField("othergridtype")
    private Byte othergridtype;

    @TableField("mainsubjecttype")
    private Byte mainsubjecttype;

    @TableField("othersubjecttype")
    private Byte othersubjecttype;

    @TableField("summaryno")
    private Integer summaryno;

    @TableField("ProcessGridType")
    private Short processGridType;

    @TableField("ProcessSubjectType")
    private Short processSubjectType;

    @TableField("cNdxTableName")
    private String cNdxTableName;

    @TableField("cDlyTableName")
    private String cDlyTableName;

    @TableField("Caption")
    private String caption;
}
