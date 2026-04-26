package thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.extend;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 采购订单追踪 VO（前端交互对象） 对应 DlyBuy + Dlystock 联表查询 + 字典转译 结果集
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@Data
@NoArgsConstructor
public class PurchaseTraceOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========================== 进销存索引总表 Dlyndx 字段 ==========================
    /** 进销存单据编号 */
    String number;
    /** 进销存单据摘要 */
    String summary;

    // ========================== 采购明细表 DlyBuy 字段(a_) ==========================
    /** 单据号 */
    private Long aVchcode;
    /** 科目typeid */
    private String aAtypeid;
    /** 往来单位typeid */
    private String aBtypeid;
    /** 职员typeid */
    private String aEtypeid;
    /** 仓库typeid */
    private String aKtypeid;
    /** 存货typeid */
    private String aPtypeId;
    /** 数量 */
    private BigDecimal aQty;
    /** 折扣 */
    private BigDecimal aDiscount;
    /** 折后单价 */
    private Double aDiscountPrice;
    /** 成本金额 */
    private BigDecimal aCosttotal;
    /** 成本单价 */
    private BigDecimal aCostprice;
    /** 批号 */
    private String aBlockno;
    /** 折前单价 */
    private Double aPrice;
    /** 折前金额 */
    private BigDecimal aTotal;
    /** 到期日期 */
    private String aProdate;
    /** 含税单价 */
    private Double aTaxPrice;
    /** 税款 */
    private BigDecimal aTaxtotal;
    /** 行摘要 */
    private String aComment;
    /** 单据日期 */
    private String aDate;
    /** 表格位置 1主表格 2钱流单等把表格外数据作为明细记录的 5 赠品 6 销售单抹零 7次表格 */
    private String aUsedtype;
    /** 会计期间 */
    private Integer aPeriod;
    /** 含税金额 */
    private BigDecimal aTaxTotal;
    /** 税率 */
    private BigDecimal aTax;
    /** 折后金额 */
    private BigDecimal aDiscounttotal;
    /** 单据类型 */
    private Integer aVchtype;
    /** 红字标志 */
    private String aRedword;
    /** 红字标志 */
    private String aRedwordName;
    /** 明细序号 */
    private Long aDlyorder;
    /** 单位 */
    private Integer aUnit;
    /** 库存类型 用于区分实物仓库和账面库存 */
    private Integer aPdetail;
    /** 库存类型 用于区分实物仓库和账面库存 */
    private String aPdetailName;
    /** 源明细序号 */
    private Integer aSourceDlyOrder;
    /** 到货数量 */
    private BigDecimal aToqty;
    /** 红冲标志。RedWord = 'T' and RedOld='T'单据是红字单据;仅RedOld='T'该单据是被红冲单据 */
    private String aRedOld;
    /** 副单位数量 */
    private BigDecimal aQtyOther;
    /** 源(进货订单)单号 */
    private Long aSourceVchcode;
    /** 源(进货订单)单据类型 */
    private Integer aSourceVchtype;
    /** 未知字段 */
    private Integer aRowNo;
    /** 部门id */
    private String aProjectID;
    /** 制定成本 */
    private Byte aAppoint;
    /** 单据类型 1、草稿 2、正式单据 4、审核中单据 */
    private Byte aDraft;
    /** 费用金额 */
    private BigDecimal aFeeTotal;
    /** 结算单位 */
    private String aSettleBtypeId;
    /** 明细标记 */
    private Byte aDetailSign;
    /** 发票类型 */
    private String aMtypeid;
    /** 父单据类型 */
    private Integer aParVchtype;
    /** 核定到货数量 */
    private BigDecimal aAuditToQty;
    /** 退还原因 */
    private String aReturnReason;
    /** 存货自定义项01 */
    private String aUserDefined01;
    /** 存货自定义项02 */
    private String aUserDefined02;
    /** 未知字段 */
    private Short aWldz;
    /** 未知字段 */
    private BigDecimal aToOutQty;
    /** 未知字段 */
    private String aInNumber;
    /** 未知字段 */
    private Long aInVchCode;
    /** 未知字段 */
    private Short aInVchType;
    /** 未知字段 */
    private Long aInDlyorder;
    /** 未知字段 */
    private Byte aCalcCostOrder;
    /** 自由项01 */
    private String aFreeDom01;
    /** 自由项02 */
    private String aFreeDom02;
    /** 自由项03 */
    private String aFreeDom03;
    /** 自由项04 */
    private String aFreeDom04;
    /** 自由项05 */
    private String aFreeDom05;
    /** 自由项06 */
    private String aFreeDom06;
    /** 自由项07 */
    private String aFreeDom07;
    /** 自由项08 */
    private String aFreeDom08;
    /** 自由项09 */
    private String aFreeDom09;
    /** 自由项10 */
    private String aFreeDom10;
    /** 自由项11 */
    private String aFreeDom11;
    /** 自由项12 */
    private String aFreeDom12;
    /** 自由项13 */
    private String aFreeDom13;
    /** 自由项14 */
    private BigDecimal aFreeDom14;
    /** 自由项15 */
    private BigDecimal aFreeDom15;
    /** 自由项16 */
    private BigDecimal aFreeDom16;
    /** 未知字段 */
    private Integer aFreeDomDateDif;
    /** 未知字段 */
    private String aCustom3;
    /** 未知字段 */
    private String aCustom4;
    /** 未知字段 */
    private Integer aSourceVchtype2;
    /** 未知字段 */
    private Integer aSourceVchcode2;
    /** 未知字段 */
    private Integer aSourceDlyOrder2;
    /** 未知字段 */
    private String aBtypeOtherCode;
    /** 未知字段 */
    private String aProduceDate;
    /** 未知字段 */
    private BigDecimal aIniInStockToQty;
    /** 未知字段 */
    private BigDecimal aIniInStockBackQty;
    /** 未知字段 */
    private BigDecimal aInStockToQty;
    /** 未知字段 */
    private BigDecimal aInStockBackQty;
    /** 未知字段 */
    private Byte aInstockBackSign;
    /** 未知字段 */
    private BigDecimal aIniInstockToQtyOther;
    /** 未知字段 */
    private BigDecimal aIniToFactQty;
    /** 未知字段 */
    private String aBtypeOtherName;
    /** 未知字段 */
    private String aBtypeOtherStandardType;
    /** 未知字段 */
    private BigDecimal aAuditInstockToQty;

    // ========================== 库存明细表 Dlystock 字段(b_) ==========================
    /** 单据号 */
    private Long bVchcode;
    /** 往来单位typeid */
    private String bBtypeid;
    /** 职员typeid */
    private String bEtypeid;
    /** 仓库typeid */
    private String bKtypeid;
    /** 存货typeid */
    private String bPtypeId;
    /** 数量 */
    private BigDecimal bQty;
    /** 批号 */
    private String bBlockno;
    /** 到期日期 */
    private String bProdate;
    /** 行摘要 */
    private String bComment;
    /** 单据日期 */
    private String bDate;
    /** 表格位置 1主表格 2钱流单等把表格外数据作为明细记录的 5 赠品 6 销售单抹零 7次表格 */
    private String bUsedtype;
    /** 会计期间 */
    private Integer bPeriod;
    /** 单据类型 */
    private Integer bVchtype;
    /** 明细序号 */
    private Long bDlyorder;
    /** 单位 */
    private Integer bUnit;
    /** 源明细序号 */
    private Integer bSourceDlyOrder;
    /** 转入仓库typeid */
    private String bKtypeid2;
    /** 源单据类型 */
    private Integer bSourceVchType;
    /** 存货自定义项01 */
    private String bUserDefined01;
    /** 存货自定义项02 */
    private String bUserDefined02;
    /** 自由项01 */
    private String bFreeDom01;
    /** 自由项02 */
    private String bFreeDom02;
    /** 自由项03 */
    private String bFreeDom03;
    /** 自由项04 */
    private String bFreeDom04;
    /** 自由项05 */
    private String bFreeDom05;
    /** 自由项06 */
    private String bFreeDom06;
    /** 自由项07 */
    private String bFreeDom07;
    /** 自由项08 */
    private String bFreeDom08;
    /** 自由项09 */
    private String bFreeDom09;
    /** 自由项10 */
    private String bFreeDom10;
    /** 源(进货订单)单号 */
    private Long bSourceVchcode;
    /** 未知字段 */
    private Integer bRowNo;
    /** 库位 */
    private String bPosition;
    /** 库位2 */
    private String bPosition2;
    /** 单据类型 1、草稿 2、正式单据 4、审核中单据 */
    private Byte bDraft;
    /** 自由项11 */
    private String bFreeDom11;
    /** 自由项12 */
    private String bFreeDom12;
    /** 自由项13 */
    private String bFreeDom13;
    /** 自由项14 */
    private BigDecimal bFreeDom14;
    /** 自由项15 */
    private BigDecimal bFreeDom15;
    /** 自由项16 */
    private BigDecimal bFreeDom16;
    /** 未知字段 */
    private Integer bFreeDomDateDif;
    /** 副单位数量 */
    private BigDecimal bQtyOther;
    /** 未知字段 */
    private String bCustom3;
    /** 未知字段 */
    private String bCustom4;
    /** 未知字段 */
    private String bBtypeOtherCode;
    /** 未知字段 */
    private String bProduceDate;
    /** 发票类型 */
    private String bMtypeid;
    /** 父单据类型 */
    private Integer bParVchtype;
    /** 未知字段 */
    private String bBtypeOtherName;
    /** 未知字段 */
    private String bBtypeOtherStandardType;

    // ========================== 字典转译名称字段(前端展示用) ==========================
    /** 采购单-往来单位名称 */
    private String aBtypeidFullname;
    /** 采购单-职员名称 */
    private String aEtypeidFullname;
    /** 采购单-仓库名称 */
    private String aKtypeidFullname;
    /** 采购单-存货名称 */
    private String aPtypeIdFullname;
    /** 采购单-表格位置名称 */
    private String aUsedtypeName;
    /** 采购单-单据类型名称 */
    private String aVchtypeFullname;
    /** 采购单-红冲状态名称 */
    private String aRedOldName;
    /** 采购单-部门名称 */
    private String aProjectIDFullname;
    /** 采购单-结算单位名称 */
    private String aSettleBtypeIdFullname;
    /** 采购单-发票类型名称 */
    private String aMtypeidFullname;

    /** 库存单-往来单位名称 */
    private String bBtypeidFullname;
    /** 库存单-职员名称 */
    private String bEtypeidFullname;
    /** 库存单-仓库名称 */
    private String bKtypeidFullname;
    /** 库存单-存货名称 */
    private String bPtypeIdFullname;
    /** 库存单-单据类型名称 */
    private String bVchtypeFullname;
    /** 库存单-发票类型名称 */
    private String bMtypeidFullname;
    /** 库存单-表格位置名称 */
    private String bUsedtypeName;
}