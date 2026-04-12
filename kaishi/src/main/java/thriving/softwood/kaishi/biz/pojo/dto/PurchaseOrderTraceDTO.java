package thriving.softwood.kaishi.biz.pojo.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import thriving.softwood.common.core.pojo.dto.SortItemDTO;

/**
 * @author ThrivingSoftwood
 */
@Data
@NoArgsConstructor
public class PurchaseOrderTraceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 查询全量到货标记 */
    private Boolean queryPurchased;

    /** 排序条件, key : field, value : ascFlag */
    private List<SortItemDTO> sortInfo;

    /* 根据 sortInfo 拼接出来的排序 sql */
    private String orderBySql;

    /** 分页相关 */
    private Long offset;
    private Long pageNo;
    private Long pageSize;

    /** 订单号 */
    private String number;

    /** 单据号 */
    private Long vchcode;

    /** 科目typeid */
    private String atypeid;

    /** 往来单位typeid */
    private String btypeid;

    /** 职员typeid */
    private String etypeid;

    /** 仓库typeid */
    private String ktypeid;

    /** 存货typeid */
    private String ptypeId;

    /** 批号 */
    private String blockno;

    /** 到期日期-起始 */
    private String minProdate;

    /** 到期日期-结束 */
    private String maxProdate;

    /** 行摘要 */
    private String comment;

    /** 单据日期-起始 */
    private String minDate;

    /** 单据日期-结束 */
    private String maxDate;

    /** 表格位置 1主表格 2钱流单等把表格外数据作为明细记录的 5 赠品 6 销售单抹零 7次表格 */
    private String usedtype;

    /** 会计期间 */
    private Integer period;

    /** 最早会计期间 */
    private Integer minPeriod;

    /** 最迟会计期间 */
    private Integer maxPeriod;

    /** 单据类型 */
    private Integer vchtype;

    /** 明细序号 */
    private Long dlyorder;

    /** 源明细序号 */
    private Integer sourceDlyOrder;

    /** 源(进货订单)单号 */
    private Long sourceVchcode;

    /** 源(进货订单)单据类型 */
    private Integer sourceVchtype;

    /** 部门id */
    private String projectId;

    /** 单据类型 1、草稿 2、正式单据（没有审核流程的单据和审核流程走完了的单据）4、审核中单据 */
    private Byte draft;

    /** 结算单位 */
    private String settleBtypeId;

    /** 发票类型 */
    private String mtypeid;

    /** 父单据类型 */
    private Integer parVchtype;
}