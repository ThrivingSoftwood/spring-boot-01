package thriving.softwood.kaishi.biz.pojo.vo;

import static thriving.softwood.kaishi.biz.constant.BaseConst.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;
import thriving.softwood.common.core.annotation.FieldPerm;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.extend.PurchaseTraceOrder;

/**
 * <p>
 * 采购订单追踪 VO（前端展示对象） 核心逻辑：基于采购明细(a)与库存实绩(b)进行对撞显示
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-07
 */
@Data
@NoArgsConstructor
public class PurchaseTraceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public PurchaseTraceVO(PurchaseTraceOrder obj) {
        number = obj.getNumber();
        summary = obj.getSummary();
        aDate = obj.getADate();
        aVchcode = obj.getAVchcode();
        aDlyOrder = obj.getADlyorder();
        aBtypeidFullname = obj.getABtypeidFullname();
        aPtypeIdFullname = obj.getAPtypeIdFullname();
        // todo 不知道 unit 到底怎么转译,先直接转成 string
        aUnitName = String.valueOf(obj.getAUnit());
        aQty = obj.getAQty();
        aPrice = obj.getAPrice();
        aDiscounttotal = obj.getADiscounttotal();
        aComment = obj.getAComment();
        aRedOldName = obj.getARedOldName();
        bQty = obj.getBQty();
        bDate = obj.getBDate();
        bVchcode = obj.getBVchcode();
        bDlyOrder = obj.getBDlyorder();
        bKtypeidFullname = obj.getBKtypeidFullname();
        aEtypeidFullname = obj.getAEtypeidFullname();
        aBlockno = obj.getABlockno();

        BigDecimal bQty = Optional.ofNullable(obj.getBQty()).orElse(BigDecimal.ZERO);
        owedQty = obj.getAQty().subtract(bQty);

        if (bQty.compareTo(BigDecimal.ZERO) == 0) {
            statusName = STATUS_PENDING_ARRIVAL;
            statusTag = STATUS_TAG_WARNING;
        } else if (owedQty.compareTo(BigDecimal.ZERO) > 0) {
            statusName = STATUS_PARTIAL_RECEIPT;
            statusTag = STATUS_TAG_WARNING;
        } else if (owedQty.compareTo(BigDecimal.ZERO) < 0) {
            statusName = STATUS_OVER_RECEIPT;
            statusTag = STATUS_TAG_DANGER;
        } else {
            statusName = STATUS_FULLY_RECEIVED;
            statusTag = STATUS_TAG_SUCCESS;
        }
    }

    // ========================== 1. 状态展示字段 (后端计算) ==========================

    /**
     * 订单状态名称：未到货、部分到货、已完成 逻辑：根据 aQty 与 bQty 的比对结果生成
     */
    private String statusName;

    /**
     * 状态标签颜色：danger(红), warning(黄), success(绿) 适配 Element Plus 的 el-tag 类型
     */
    private String statusTag;

    /**
     * 欠交数量 (计算字段) 逻辑：aQty - bQty
     */
    private BigDecimal owedQty;

    // ========================== 2. 采购计划信息 (A表核心+进销存简要信息) ==========================

    /** 进销存单据编号 */
    String number;

    /** 进销存单据摘要 */
    String summary;

    /** 采购日期 */
    private String aDate;

    /** 采购单号 */
    private Long aVchcode;

    /**
     * 单据序号
     */
    private Long aDlyOrder;

    /** 供应商全称 */
    @FieldPerm("purchase:btype:view")
    private String aBtypeidFullname;

    /** 存货/商品名称 */
    private String aPtypeIdFullname;

    /** 单位名称 */
    private String aUnitName;

    /** 采购计划数量 */
    private BigDecimal aQty;

    /** 采购单价 (折前) */
    @FieldPerm("purchase:price:view")
    private Double aPrice;

    /** 折后总金额 (实际应付) */
    @FieldPerm("purchase:price:view")
    private BigDecimal aDiscounttotal;

    /** 采购备注 */
    private String aComment;

    /** 红冲状态名称 (如：被红冲单据) */
    private String aRedOldName;

    // ========================== 3. 入库执行信息 (B表核心) ==========================

    /** 实际入库数量 (汇总后的数量) */
    private BigDecimal bQty;

    /** 最近一次入库日期 */
    private String bDate;

    /** 实际入库单号 */
    private Long bVchcode;

    /**
     * 单据序号
     */
    private Long bDlyOrder;

    /** 入库仓库名称 */
    private String bKtypeidFullname;

    // ========================== 4. 扩展信息 (按需展示) ==========================

    /** 采购员名称 */
    private String aEtypeidFullname;

    /** 批号 */
    private String aBlockno;
}