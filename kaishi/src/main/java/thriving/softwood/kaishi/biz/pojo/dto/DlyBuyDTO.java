package thriving.softwood.kaishi.biz.pojo.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 采购单明细 DTO 对应查询字段：dlybuy 表查询结果
 */
@Data // 自动生成get/set/toString/构造方法
@NoArgsConstructor
public class DlyBuyDTO {

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
    private String btypeId;

    /**
     * 产品类型ID
     */
    private String ptypeId;

    /**
     * 单位
     */
    private Integer unit;

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
    private String etypeId;

    /**
     * 栋号
     */
    private String blockNo;
}