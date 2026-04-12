package thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 采购明细表(DlyBuy)实体类
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-06
 */
@Data
@NoArgsConstructor
@TableName("DlyBuy")
public class DlyBuy implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 单据号 */
    @TableField("Vchcode")
    private Long vchcode;

    /** 科目typeid */
    @TableField("atypeid")
    private String atypeid;

    /** 往来单位typeid */
    @TableField("btypeid")
    private String btypeid;

    /** 职员typeid */
    @TableField("etypeid")
    private String etypeid;

    /** 仓库typeid */
    @TableField("ktypeid")
    private String ktypeid;

    /** 存货typeid */
    @TableField("PtypeId")
    private String ptypeId;

    /** 数量 */
    @TableField("Qty")
    private BigDecimal qty;

    /** 折扣 */
    @TableField("discount")
    private BigDecimal discount;

    /** 折后单价 */
    @TableField("DiscountPrice")
    private Double discountPrice;

    /** 成本金额 */
    @TableField("costtotal")
    private BigDecimal costtotal;

    /** 成本单价 */
    @TableField("costprice")
    private BigDecimal costprice;

    /** 批号 */
    @TableField("Blockno")
    private String blockno;

    /** 折前单价 */
    @TableField("price")
    private Double price;

    /** 折前金额 */
    @TableField("total")
    private BigDecimal total;

    /** 到期日期 */
    @TableField("Prodate")
    private String prodate;

    /** 含税单价 */
    @TableField("TaxPrice")
    private Double taxPrice;

    /** 税款, total 的首字母小写 */
    @TableField("TaxTotal")
    private BigDecimal taxtotal;

    /** 行摘要 */
    @TableField("comment")
    private String comment;

    /** 单据日期 */
    @TableField("date")
    private String date;

    /** 表格位置 1主表格 2钱流单等把表格外数据作为明细记录的 5 赠品 6 销售单抹零 7次表格 */
    @TableField("usedtype")
    private String usedtype;

    /** 会计期间 */
    @TableField("period")
    private Integer period;

    /** 含税金额 */
    @TableField("tax_total")
    private BigDecimal taxTotal;

    /** 税率 */
    @TableField("tax")
    private BigDecimal tax;

    /** 折后金额 */
    @TableField("discounttotal")
    private BigDecimal discounttotal;

    /** 单据类型 */
    @TableField("Vchtype")
    private Integer vchtype;

    /** 红字标志 */
    @TableField("redword")
    private String redword;

    /** 明细序号 */
    @TableField("dlyorder")
    private Long dlyorder;

    /** 单位 */
    @TableField("unit")
    private Integer unit;

    /** 库存类型 用于区分实物仓库和账面库存 */
    @TableField("PDETAIL")
    private Integer pdetail;

    /** 源明细序号 */
    @TableField("SourceDlyOrder")
    private Integer sourceDlyOrder;

    /** 到货数量 */
    @TableField("Toqty")
    private BigDecimal toqty;

    /** 红冲标志。RedWord = 'T' and RedOld='T'单据是红字单据;仅RedOld='T'该单据是被红冲单据 */
    @TableField("RedOld")
    private String redOld;

    /** 存货自定义项01 */
    @TableField("UserDefined01")
    private String userDefined01;

    /** 存货自定义项02 */
    @TableField("UserDefined02")
    private String userDefined02;

    /** 未知字段 */
    @TableField("WLDZ")
    private Short wldz;

    /** 副单位数量 */
    @TableField("QtyOther")
    private BigDecimal qtyOther;

    /** 自由项01 */
    @TableField("FreeDom01")
    private String freeDom01;

    /** 自由项02 */
    @TableField("FreeDom02")
    private String freeDom02;

    /** 自由项03 */
    @TableField("FreeDom03")
    private String freeDom03;

    /** 自由项04 */
    @TableField("FreeDom04")
    private String freeDom04;

    /** 自由项05 */
    @TableField("FreeDom05")
    private String freeDom05;

    /** 自由项06 */
    @TableField("FreeDom06")
    private String freeDom06;

    /** 自由项07 */
    @TableField("FreeDom07")
    private String freeDom07;

    /** 自由项08 */
    @TableField("FreeDom08")
    private String freeDom08;

    /** 自由项09 */
    @TableField("FreeDom09")
    private String freeDom09;

    /** 自由项10 */
    @TableField("FreeDom10")
    private String freeDom10;

    /** 未知字段 */
    @TableField("ToOutQty")
    private BigDecimal toOutQty;

    /** 未知字段 */
    @TableField("InNumber")
    private String inNumber;

    /** 未知字段 */
    @TableField("InVchCode")
    private Long inVchCode;

    /** 未知字段 */
    @TableField("InVchType")
    private Short inVchType;

    /** 未知字段 */
    @TableField("InDlyorder")
    private Long inDlyorder;

    /** 源(进货订单)单号 */
    @TableField("SourceVchcode")
    private Long sourceVchcode;

    /** 源(进货订单)单据类型 */
    @TableField("SourceVchtype")
    private Integer sourceVchtype;

    /** 未知字段 */
    @TableField("RowNo")
    private Integer rowNo;

    /** 部门id */
    @TableField("ProjectID")
    private String projectID;

    /** 制定成本 */
    @TableField("Appoint")
    private Byte appoint;

    /** 单据类型 1、草稿 2、正式单据（无审核流程/审核完成）4、审核中单据 */
    @TableField("Draft")
    private Byte draft;

    /** 费用金额 */
    @TableField("FeeTotal")
    private BigDecimal feeTotal;

    /** 未知字段 */
    @TableField("CalcCostOrder")
    private Byte calcCostOrder;

    /** 自由项11 */
    @TableField("FreeDom11")
    private String freeDom11;

    /** 自由项12 */
    @TableField("FreeDom12")
    private String freeDom12;

    /** 自由项13 */
    @TableField("FreeDom13")
    private String freeDom13;

    /** 自由项14 */
    @TableField("FreeDom14")
    private BigDecimal freeDom14;

    /** 自由项15 */
    @TableField("FreeDom15")
    private BigDecimal freeDom15;

    /** 自由项16 */
    @TableField("FreeDom16")
    private BigDecimal freeDom16;

    /** 未知字段 */
    @TableField("FreeDomDateDif")
    private Integer freeDomDateDif;

    /** 未知字段 */
    @TableField("Custom3")
    private String custom3;

    /** 未知字段 */
    @TableField("Custom4")
    private String custom4;

    /** 未知字段 */
    @TableField("SourceVchtype2")
    private Integer sourceVchtype2;

    /** 未知字段 */
    @TableField("SourceVchcode2")
    private Integer sourceVchcode2;

    /** 未知字段 */
    @TableField("SourceDlyOrder2")
    private Integer sourceDlyOrder2;

    /** 结算单位 */
    @TableField("SettleBtypeId")
    private String settleBtypeId;

    /** 未知字段 */
    @TableField("BtypeOtherCode")
    private String btypeOtherCode;

    /** 明细标记 */
    @TableField("DetailSign")
    private Byte detailSign;

    /** 未知字段 */
    @TableField("ProduceDate")
    private String produceDate;

    /** 未知字段 */
    @TableField("IniInStockToQty")
    private BigDecimal iniInStockToQty;

    /** 未知字段 */
    @TableField("IniInStockBackQty")
    private BigDecimal iniInStockBackQty;

    /** 未知字段 */
    @TableField("InStockToQty")
    private BigDecimal inStockToQty;

    /** 未知字段 */
    @TableField("InStockBackQty")
    private BigDecimal inStockBackQty;

    /** 未知字段 */
    @TableField("InstockBackSign")
    private Byte instockBackSign;

    /** 未知字段 */
    @TableField("IniInstockToQtyOther")
    private BigDecimal iniInstockToQtyOther;

    /** 发票类型 */
    @TableField("mtypeid")
    private String mtypeid;

    /** 未知字段 */
    @TableField("IniToFactQty")
    private BigDecimal iniToFactQty;

    /** 父单据类型 */
    @TableField("ParVchtype")
    private Integer parVchtype;

    /** 未知字段 */
    @TableField("BtypeOtherName")
    private String btypeOtherName;

    /** 未知字段 */
    @TableField("BtypeOtherStandardType")
    private String btypeOtherStandardType;

    /** 核定到货数量 */
    @TableField("AuditToQty")
    private BigDecimal auditToQty;

    /** 未知字段 */
    @TableField("AuditInstockToQty")
    private BigDecimal auditInstockToQty;

    /** 退还原因 */
    @TableField("ReturnReason")
    private String returnReason;
}