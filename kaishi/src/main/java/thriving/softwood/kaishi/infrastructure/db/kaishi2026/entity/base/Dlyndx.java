package thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("Dlyndx")
public class Dlyndx implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("Vchcode")
    private Long vchcode;

    @TableField("DATE")
    private String date;

    @TableField("NUMBER")
    private String number;

    @TableField("VchType")
    private Integer vchType;

    @TableField("summary")
    private String summary;

    @TableField("btypeid")
    private String btypeid;

    @TableField("etypeid")
    private String etypeid;

    @TableField("ktypeid")
    private String ktypeid;

    @TableField("ktypeid2")
    private String ktypeid2;

    @TableField("ifcheck")
    private String ifcheck;

    @TableField("period")
    private Integer period;

    @TableField("RedWord")
    private String redWord;

    @TableField("RedOld")
    private String redOld;

    @TableField("InputNo")
    private String inputNo;

    @TableField("draft")
    private Integer draft;

    @TableField("Total")
    private BigDecimal total;

    @TableField("GatheringDate")
    private String gatheringDate;

    @TableField("projectid")
    private String projectid;

    @TableField("ETypeID2")
    private String eTypeID2;

    @TableField("SourceVchcode")
    private Integer sourceVchcode;

    @TableField("SourceVchType")
    private Integer sourceVchType;

    @TableField("BillTotal")
    private BigDecimal billTotal;

    @TableField("UserDefined01")
    private String userDefined01;

    @TableField("UserDefined02")
    private String userDefined02;

    @TableField("UserDefined03")
    private String userDefined03;

    @TableField("BTypeID2")
    private String bTypeID2;

    @TableField("AuditLeveal")
    private Short auditLeveal;

    @TableField("ProjectId2")
    private String projectId2;

    @TableField("WtypeId")
    private String wtypeId;

    @TableField("Compact")
    private String compact;

    @TableField("MatchQty")
    private BigDecimal matchQty;

    @TableField("Pubufts")
    private byte[] pubufts;

    @TableField("SaveTime")
    private LocalDateTime saveTime;

    @TableField("DifAType")
    private Integer difAType;

    @TableField("Appoint")
    private Byte appoint;

    @TableField("Acc")
    private Integer acc;

    @TableField("NoteCode")
    private String noteCode;

    @TableField("isPreARAP")
    private Byte isPreARAP;

    @TableField("InvoiceType")
    private Byte invoiceType;

    @TableField("CancelType")
    private Byte cancelType;

    @TableField("SettleType")
    private Byte settleType;

    @TableField("NormCost")
    private Byte normCost;

    @TableField("UserDefined04")
    private String userDefined04;

    @TableField("UserDefined05")
    private String userDefined05;

    @TableField("UserDefined06")
    private String userDefined06;

    @TableField("UserDefined07")
    private String userDefined07;

    @TableField("UserDefined08")
    private String userDefined08;

    @TableField("UserDefined09")
    private String userDefined09;

    @TableField("UserDefined10")
    private String userDefined10;

    @TableField("UserDefined11")
    private String userDefined11;

    @TableField("UserDefined12")
    private String userDefined12;

    @TableField("UserDefined13")
    private String userDefined13;

    @TableField("UserDefined14")
    private BigDecimal userDefined14;

    @TableField("UserDefined15")
    private BigDecimal userDefined15;

    @TableField("UserDefined16")
    private BigDecimal userDefined16;

    @TableField("SettleBtypeId")
    private String settleBtypeId;

    @TableField("IsCheckOut")
    private Boolean isCheckOut;

    @TableField("ModifyPerson")
    private String modifyPerson;

    @TableField("ModifyDate")
    private LocalDateTime modifyDate;

    @TableField("LastYear")
    private Byte lastYear;

    @TableField("BuildType")
    private Integer buildType;

    @TableField("BuildNo")
    private Integer buildNo;

    @TableField("IsAutoBuild")
    private Byte isAutoBuild;

    @TableField("InstockBackSign")
    private Byte instockBackSign;

    @TableField("AuditDate")
    private LocalDateTime auditDate;

    @TableField("mtypeid")
    private String mtypeid;

    @TableField("BillType")
    private Integer billType;

    @TableField("IsRejected")
    private Boolean isRejected;

    @TableField("ParVchtype")
    private Integer parVchtype;

    @TableField("DataCheck")
    private String dataCheck;

    @TableField("SysUserName")
    private String sysUserName;

    @TableField("BillFrom")
    private Integer billFrom;

    @TableField("SourceNumber")
    private String sourceNumber;

    @TableField("AuditTime")
    private String auditTime;

    @TableField("SourceVchtype2")
    private Integer sourceVchtype2;

    @TableField("SourceVchcode2")
    private Long sourceVchcode2;
}
