package thriving.softwood.customer.first.infrastructure.db.kaishi2026.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

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
@TableName("Dlystock")
public class Dlystock implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("Vchcode")
    private Long vchcode;

    @TableField("btypeid")
    private String btypeid;

    @TableField("etypeid")
    private String etypeid;

    @TableField("ktypeid")
    private String ktypeid;

    @TableField("PtypeId")
    private String ptypeId;

    @TableField("Qty")
    private BigDecimal qty;

    @TableField("Blockno")
    private String blockno;

    @TableField("Prodate")
    private String prodate;

    @TableField("comment")
    private String comment;

    @TableField("date")
    private String date;

    @TableField("usedtype")
    private String usedtype;

    @TableField("period")
    private Integer period;

    @TableField("Vchtype")
    private Integer vchtype;

    @TableField("dlyorder")
    private Long dlyorder;

    @TableField("unit")
    private Integer unit;

    @TableField("SourceDlyOrder")
    private Integer sourceDlyOrder;

    @TableField("ktypeid2")
    private String ktypeid2;

    @TableField("SourceVchType")
    private Integer sourceVchType;

    @TableField("UserDefined01")
    private String userDefined01;

    @TableField("UserDefined02")
    private String userDefined02;

    @TableField("FreeDom01")
    private String freeDom01;

    @TableField("FreeDom02")
    private String freeDom02;

    @TableField("FreeDom03")
    private String freeDom03;

    @TableField("FreeDom04")
    private String freeDom04;

    @TableField("FreeDom05")
    private String freeDom05;

    @TableField("FreeDom06")
    private String freeDom06;

    @TableField("FreeDom07")
    private String freeDom07;

    @TableField("FreeDom08")
    private String freeDom08;

    @TableField("FreeDom09")
    private String freeDom09;

    @TableField("FreeDom10")
    private String freeDom10;

    @TableField("SourceVchcode")
    private Long sourceVchcode;

    @TableField("RowNo")
    private Integer rowNo;

    @TableField("Position")
    private String position;

    @TableField("Position2")
    private String position2;

    @TableField("Draft")
    private Byte draft;

    @TableField("FreeDom11")
    private String freeDom11;

    @TableField("FreeDom12")
    private String freeDom12;

    @TableField("FreeDom13")
    private String freeDom13;

    @TableField("FreeDom14")
    private BigDecimal freeDom14;

    @TableField("FreeDom15")
    private BigDecimal freeDom15;

    @TableField("FreeDom16")
    private BigDecimal freeDom16;

    @TableField("FreeDomDateDif")
    private Integer freeDomDateDif;

    @TableField("QtyOther")
    private BigDecimal qtyOther;

    @TableField("Custom3")
    private String custom3;

    @TableField("Custom4")
    private String custom4;

    @TableField("BtypeOtherCode")
    private String btypeOtherCode;

    @TableField("ProduceDate")
    private String produceDate;

    @TableField("mtypeid")
    private String mtypeid;

    @TableField("ParVchtype")
    private Integer parVchtype;

    @TableField("BtypeOtherName")
    private String btypeOtherName;

    @TableField("BtypeOtherStandardType")
    private String btypeOtherStandardType;
}
