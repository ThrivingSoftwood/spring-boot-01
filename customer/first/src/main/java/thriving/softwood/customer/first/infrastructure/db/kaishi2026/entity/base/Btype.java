package thriving.softwood.customer.first.infrastructure.db.kaishi2026.entity.base;

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
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@Data
@NoArgsConstructor
@TableName("Btype")
public class Btype implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("typeId")
    private String typeId;

    @TableField("parid")
    private String parid;

    @TableField("leveal")
    private Short leveal;

    @TableField("soncount")
    private Integer soncount;

    @TableField("sonnum")
    private Integer sonnum;

    @TableField("UserCode")
    private String userCode;

    @TableField("FullName")
    private String fullName;

    @TableField("Name")
    private String name;

    @TableField("Area")
    private String area;

    @TableField("TelAndAddress")
    private String telAndAddress;

    @TableField("PostCode")
    private String postCode;

    @TableField("Person")
    private String person;

    @TableField("TaxNumber")
    private String taxNumber;

    @TableField("BankAndAcount")
    private String bankAndAcount;

    @TableField("Comment")
    private String comment;

    @TableField("ARTotal")
    private BigDecimal aRTotal;

    @TableField("APTotal")
    private BigDecimal aPTotal;

    @TableField("ARTotal00")
    private BigDecimal aRTotal00;

    @TableField("APTotal00")
    private BigDecimal aPTotal00;

    @TableField("deleted")
    private Boolean deleted;

    @TableField("Namepy")
    private String namepy;

    @TableField("Rec")
    private Integer rec;

    @TableField("ParRec")
    private Integer parRec;

    @TableField("isclient")
    private Integer isclient;

    @TableField("arlimit")
    private BigDecimal arlimit;

    @TableField("aplimit")
    private BigDecimal aplimit;

    @TableField("AreaTypeID")
    private String areaTypeID;

    @TableField("otherar")
    private BigDecimal otherar;

    @TableField("otherap")
    private BigDecimal otherap;

    @TableField("arsaletax")
    private BigDecimal arsaletax;

    @TableField("apbuytax")
    private BigDecimal apbuytax;

    @TableField("preprice")
    private Short preprice;

    @TableField("fax")
    private String fax;

    @TableField("otherar00")
    private BigDecimal otherar00;

    @TableField("otherap00")
    private BigDecimal otherap00;

    @TableField("arsaletax00")
    private BigDecimal arsaletax00;

    @TableField("apbuytax00")
    private BigDecimal apbuytax00;

    @TableField("SendGoods")
    private BigDecimal sendGoods;

    @TableField("SendGoods00")
    private BigDecimal sendGoods00;

    @TableField("SendGoodsJ")
    private BigDecimal sendGoodsJ;

    @TableField("SendGoodsJ00")
    private BigDecimal sendGoodsJ00;

    @TableField("OrderNo")
    private Integer orderNo;

    @TableField("LastOperaterID")
    private String lastOperaterID;

    @TableField("LastOperatDay")
    private String lastOperatDay;

    @TableField("RDate")
    private Integer rDate;

    @TableField("BankAcount")
    private String bankAcount;

    @TableField("Email")
    private String email;

    @TableField("PreApTotal00")
    private BigDecimal preApTotal00;

    @TableField("PreARTotal00")
    private BigDecimal preARTotal00;

    @TableField("PreARTotal")
    private BigDecimal preARTotal;

    @TableField("PreAPTotal")
    private BigDecimal preAPTotal;

    @TableField("UpdateTag")
    private Integer updateTag;

    @TableField("EtypeID")
    private String etypeID;

    @TableField("isWeiWai")
    private Integer isWeiWai;

    @TableField("WeiWaiAPTotal00")
    private BigDecimal weiWaiAPTotal00;

    @TableField("WeiWaiPreAPTotal00")
    private BigDecimal weiWaiPreAPTotal00;

    @TableField("WeiWaiAPTotal")
    private BigDecimal weiWaiAPTotal;

    @TableField("WeiWaiPreAPTotal")
    private BigDecimal weiWaiPreAPTotal;

    @TableField("PrePriceType")
    private Integer prePriceType;

    @TableField("AttnID")
    private String attnID;

    @TableField("TaxRegNum")
    private String taxRegNum;

    @TableField("SettleBtypeId")
    private String settleBtypeId;

    @TableField("Mobile")
    private String mobile;

    @TableField("Comment1")
    private String comment1;

    @TableField("Comment2")
    private String comment2;

    @TableField("Comment3")
    private String comment3;

    @TableField("Comment4")
    private String comment4;

    @TableField("Comment5")
    private String comment5;

    @TableField("Comment6")
    private String comment6;

    @TableField("Comment7")
    private String comment7;

    @TableField("Comment8")
    private String comment8;

    @TableField("SettleMode")
    private Byte settleMode;

    @TableField("InputNo")
    private String inputNo;

    @TableField("CreateTime")
    private LocalDateTime createTime;

    @TableField("ModifyPerson")
    private String modifyPerson;

    @TableField("ModifyDate")
    private LocalDateTime modifyDate;

    @TableField("PushRec")
    private Integer pushRec;

    @TableField("bNoArLimit")
    private Boolean bNoArLimit;

    @TableField("OtherAPTotal")
    private BigDecimal otherAPTotal;

    @TableField("OtherAPTotal00")
    private BigDecimal otherAPTotal00;

    @TableField("OtherARTotal")
    private BigDecimal otherARTotal;

    @TableField("OtherARTotal00")
    private BigDecimal otherARTotal00;

    @TableField("Descript")
    private String descript;
}
