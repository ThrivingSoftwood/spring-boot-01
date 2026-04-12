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
@TableName("ptype")
public class Ptype implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("typeId")
    private String typeId;

    @TableField("ParId")
    private String parId;

    @TableField("leveal")
    private Short leveal;

    @TableField("sonnum")
    private Integer sonnum;

    @TableField("soncount")
    private Integer soncount;

    @TableField("CanModify")
    private String canModify;

    @TableField("UserCode")
    private String userCode;

    @TableField("FullName")
    private String fullName;

    @TableField("Name")
    private String name;

    @TableField("Standard")
    private String standard;

    @TableField("Type")
    private String type;

    @TableField("Area")
    private String area;

    @TableField("Unit1")
    private String unit1;

    @TableField("Unit2")
    private String unit2;

    @TableField("UnitRate1")
    private BigDecimal unitRate1;

    @TableField("UnitRate2")
    private BigDecimal unitRate2;

    @TableField("preprice1")
    private BigDecimal preprice1;

    @TableField("preprice2")
    private BigDecimal preprice2;

    @TableField("preprice3")
    private BigDecimal preprice3;

    @TableField("preprice4")
    private BigDecimal preprice4;

    @TableField("UsefulLifeMonth")
    private Short usefulLifeMonth;

    @TableField("UsefulLifeDay")
    private Short usefulLifeDay;

    @TableField("Comment")
    private String comment;

    @TableField("recPrice")
    private Double recPrice;

    @TableField("deleted")
    private Boolean deleted;

    @TableField("costmode")
    private Integer costmode;

    @TableField("Namepy")
    private String namepy;

    @TableField("warnup")
    private BigDecimal warnup;

    @TableField("warndown")
    private BigDecimal warndown;

    @TableField("Rec")
    private Integer rec;

    @TableField("ParRec")
    private Integer parRec;

    @TableField("barcode")
    private String barcode;

    @TableField("preprice5")
    private BigDecimal preprice5;

    @TableField("preprice6")
    private BigDecimal preprice6;

    @TableField("disrecprice")
    private Double disrecprice;

    @TableField("uselistcode")
    private Integer uselistcode;

    @TableField("OrderNo")
    private Integer orderNo;

    @TableField("LastOperaterID")
    private String lastOperaterID;

    @TableField("LastOperatDay")
    private String lastOperatDay;

    @TableField("AssistConfig")
    private Integer assistConfig;

    @TableField("SerialConfig")
    private Integer serialConfig;

    @TableField("Price7")
    private BigDecimal price7;

    @TableField("Price8")
    private BigDecimal price8;

    @TableField("Price9")
    private BigDecimal price9;

    @TableField("Price10")
    private BigDecimal price10;

    @TableField("GoodsProp")
    private String goodsProp;

    @TableField("WLType")
    private Integer wLType;

    @TableField("WLSource")
    private Integer wLSource;

    @TableField("MRPCalc")
    private Integer mRPCalc;

    @TableField("BeforeDay")
    private BigDecimal beforeDay;

    @TableField("DefaultStock")
    private String defaultStock;

    @TableField("MaxOrder")
    private BigDecimal maxOrder;

    @TableField("MinOrder")
    private BigDecimal minOrder;

    @TableField("MaxStock")
    private BigDecimal maxStock;

    @TableField("MinStock")
    private BigDecimal minStock;

    @TableField("ClassP2")
    private Integer classP2;

    @TableField("Btypeid")
    private String btypeid;

    @TableField("Safedaycount")
    private Integer safedaycount;

    @TableField("MinSalePrice")
    private BigDecimal minSalePrice;

    @TableField("Brandtypeid")
    private String brandtypeid;

    @TableField("price90")
    private Double price90;

    @TableField("OtherPrice")
    private BigDecimal otherPrice;

    @TableField("IsTaxPrice")
    private Boolean isTaxPrice;

    @TableField("GoodsOrder")
    private Integer goodsOrder;

    @TableField("Custom1")
    private String custom1;

    @TableField("Custom2")
    private String custom2;

    @TableField("UnitOther")
    private String unitOther;

    @TableField("UpdateTag")
    private Integer updateTag;

    @TableField("CheckCustom")
    private Short checkCustom;

    @TableField("Custom3")
    private String custom3;

    @TableField("Custom4")
    private String custom4;

    @TableField("Custom5")
    private String custom5;

    @TableField("KtypeId")
    private String ktypeId;

    @TableField("WorkShop")
    private String workShop;

    @TableField("SafeQty")
    private BigDecimal safeQty;

    @TableField("CostPrice")
    private BigDecimal costPrice;

    @TableField("BeforeDate")
    private Integer beforeDate;

    @TableField("PObject")
    private Integer pObject;

    @TableField("QuotePrice")
    private BigDecimal quotePrice;

    @TableField("WorkPlanVchCode")
    private Long workPlanVchCode;

    @TableField("bBlockNo")
    private Byte bBlockNo;

    @TableField("bCustom1")
    private Byte bCustom1;

    @TableField("bCustom2")
    private Byte bCustom2;

    @TableField("RefPrice")
    private Double refPrice;

    @TableField("ManSerialNum")
    private Byte manSerialNum;

    @TableField("bPurchase")
    private Boolean bPurchase;

    @TableField("bProduce")
    private Boolean bProduce;

    @TableField("bConsign")
    private Boolean bConsign;

    @TableField("bCustom3")
    private Byte bCustom3;

    @TableField("bCustom4")
    private Byte bCustom4;

    @TableField("Unit3")
    private String unit3;

    @TableField("UnitFz")
    private String unitFz;

    @TableField("bReceive")
    private Byte bReceive;

    @TableField("DefInUnit")
    private Byte defInUnit;

    @TableField("DefOutUnit")
    private Byte defOutUnit;

    @TableField("Barcode1")
    private String barcode1;

    @TableField("Barcode2")
    private String barcode2;

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

    @TableField("Tax")
    private BigDecimal tax;

    @TableField("ShelfLife")
    private Integer shelfLife;

    @TableField("TakeOut")
    private Boolean takeOut;

    @TableField("ProduceBeforeDay")
    private Integer produceBeforeDay;

    @TableField("ConsignBeforeDay")
    private Integer consignBeforeDay;

    @TableField("WeiWaiTax")
    private BigDecimal weiWaiTax;

    @TableField("DefPandianUnit")
    private Byte defPandianUnit;

    @TableField("InputNo")
    private String inputNo;

    @TableField("CreateTime")
    private LocalDateTime createTime;

    @TableField("ModifyPerson")
    private String modifyPerson;

    @TableField("ModifyDate")
    private LocalDateTime modifyDate;

    @TableField("NRefPrice")
    private String nRefPrice;

    @TableField("NCostPrice")
    private String nCostPrice;

    @TableField("PushRec")
    private Integer pushRec;

    @TableField("CalcPeriod")
    private Integer calcPeriod;

    @TableField("ReportCalcPeriod")
    private Integer reportCalcPeriod;

    @TableField("VeriMode")
    private Integer veriMode;

    @TableField("VeriRate")
    private BigDecimal veriRate;
}
