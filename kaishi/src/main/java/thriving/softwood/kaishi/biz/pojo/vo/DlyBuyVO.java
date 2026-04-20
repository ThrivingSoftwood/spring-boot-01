package thriving.softwood.kaishi.biz.pojo.vo;

import static thriving.softwood.kaishi.biz.constant.BaseConst.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;
import thriving.softwood.common.core.annotation.FieldPerm;
import thriving.softwood.kaishi.biz.pojo.dto.DlyBuyDTO;

@Data
@NoArgsConstructor
public class DlyBuyVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public DlyBuyVO(DlyBuyDTO dto) {
        vchCode = dto.getVchCode();
        dlyOrder = dto.getDlyOrder();
        date = dto.getDate();
        buyQty = dto.getBuyQty();
        stockedQty = dto.getStockedQty();
        price = dto.getPrice();
        discountTotal = dto.getDiscountTotal();
        comment = dto.getComment();
        blockNo = dto.getBlockNo();

        unitFullname = String.valueOf(dto.getUnit());

        BigDecimal stockQty = Optional.ofNullable(dto.getStockedQty()).orElse(BigDecimal.ZERO);
        owedQty = dto.getBuyQty().subtract(stockQty);

        if (stockQty.compareTo(BigDecimal.ZERO) == 0) {
            statusName = STATUS_PENDING_ARRIVAL;
            statusTag = STATUS_TAG_WARNING;
            return;
        }
        if (owedQty.compareTo(BigDecimal.ZERO) > 0) {
            statusName = STATUS_PARTIAL_RECEIPT;
            statusTag = STATUS_TAG_WARNING;
            return;
        }
        if (owedQty.compareTo(BigDecimal.ZERO) < 0) {
            statusName = STATUS_OVER_RECEIPT;
            statusTag = STATUS_TAG_DANGER;
            return;
        }
        statusName = STATUS_FULLY_RECEIVED;
        statusTag = STATUS_TAG_SUCCESS;
    }

    private Long total;

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

    /**
     * 单据编码
     */
    private Long vchCode;

    /**
     * 单据序号
     */
    private Long dlyOrder;

    /**
     * 单据日期
     */
    private String date;

    /**
     * 业务类型ID
     */
    @FieldPerm("purchase:btype:view")
    private String btypeFullname;

    /**
     * 产品类型ID
     */
    private String ptypeFullname;

    /**
     * 单位
     */
    private String unitFullname;

    /**
     * 采购数量（别名：buy_qty）
     */
    private BigDecimal buyQty;

    /**
     * 入库数量 (别名: stocked_qty)
     */
    private BigDecimal stockedQty;

    /**
     * 单价
     */
    @FieldPerm("purchase:price:view")
    private Double price;

    /**
     * 折扣总额
     */
    private BigDecimal discountTotal;

    /**
     * 备注
     */
    private String comment;

    /**
     * 红冲原单号
     */
    private String redOld;

    /**
     * 红冲标记
     */
    private String redWord;

    /**
     * 项目类型ID
     */
    private String etypeFullname;

    /**
     * 栋号
     */
    private String blockNo;
}
