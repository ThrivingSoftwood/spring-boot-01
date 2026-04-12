select getdate();
select suser_name();

select a.vchcode, b.vchcode, a.dlyorder, b.number
from dlybuy as a
         left join dlyndx as b on a.vchcode = b.vchcode
where a.vchtype = '34';


-- 判断订单是否正常完成
select distinct a.vchcode, a.dlyorder, sum(isnull(b.qty, 0)), a.qty
from dlybuy as a
         -- 关联该采购明细对应的所有入库记录（支持一对多）
         left join dlystock as b
                   on a.vchcode = b.sourcevchcode
                       and a.vchtype = b.sourcevchtype
                       and a.dlyorder = b.sourcedlyorder
where a.vchtype = '34'
group by a.vchcode, a.dlyorder, a.qty
-- 筛选：该采购明细 入库总数量 ≠ 采购数量（异常明细）
having sum(isnull(b.qty, 0)) != a.qty;

select sum(isnull(null, 0))
order by 1;



select a.vchcode, a.dlyorder
from dlybuy as a
         -- 关联该采购明细对应的所有入库记录（支持一对多）
         left join dlystock as b
                   on a.vchcode = b.sourcevchcode
                       and a.vchtype = b.sourcevchtype
                       and a.dlyorder = b.sourcedlyorder
where a.Vchtype = 34
group by a.vchcode, a.dlyorder, a.qty
-- 筛选：该采购明细 入库总数量 ≠ 采购数量（异常明细）
having sum(isnull(b.qty, 0)) != a.qty
order by a.vchcode, a.dlyorder, a.qty;


-- 整合字典转译后的主SQL
select
-- 原主SQL所有保留字段（未做任何修改）
a.Vchcode                as a_Vchcode,
a.atypeid                as a_atypeid,
a.btypeid                as a_btypeid,
a.etypeid                as a_etypeid,
a.ktypeid                as a_ktypeid,
a.PtypeId                as a_PtypeId,
a.Qty                    as a_Qty,
a.discount               as a_discount,
a.DiscountPrice          as a_DiscountPrice,
a.costtotal              as a_costtotal,
a.costprice              as a_costprice,
a.Blockno                as a_Blockno,
a.price                  as a_price,
a.total                  as a_total,
a.Prodate                as a_Prodate,
a.TaxPrice               as a_TaxPrice,
a.TaxTotal               as a_TaxTotal,
a.comment                as a_comment,
a.date                   as a_date,
a.usedtype               as a_usedtype,
a.period                 as a_period,
a.tax_total              as a_tax_total,
a.tax                    as a_tax,
a.discounttotal          as a_discounttotal,
a.Vchtype                as a_Vchtype,
a.redword                as a_redword,
a.dlyorder               as a_dlyorder,
a.unit                   as a_unit,
a.PDETAIL                as a_PDETAIL,
a.SourceDlyOrder         as a_SourceDlyOrder,
a.Toqty                  as a_Toqty,
a.RedOld                 as a_RedOld,
a.QtyOther               as a_QtyOther,
a.SourceVchcode          as a_SourceVchcode,
a.SourceVchtype          as a_SourceVchtype,
a.RowNo                  as a_RowNo,
a.ProjectID              as a_ProjectID,
a.Appoint                as a_Appoint,
a.Draft                  as a_Draft,
a.FeeTotal               as a_FeeTotal,
a.SettleBtypeId          as a_SettleBtypeId,
a.DetailSign             as a_DetailSign,
a.mtypeid                as a_mtypeid,
a.ParVchtype             as a_ParVchtype,
a.AuditToQty             as a_AuditToQty,
a.ReturnReason           as a_ReturnReason,
a.UserDefined01          as a_UserDefined01,
a.UserDefined02          as a_UserDefined02,
a.WLDZ                   as a_WLDZ,
a.ToOutQty               as a_ToOutQty,
a.InNumber               as a_InNumber,
a.InVchCode              as a_InVchCode,
a.InVchType              as a_InVchType,
a.InDlyorder             as a_InDlyorder,
a.CalcCostOrder          as a_CalcCostOrder,
a.FreeDom01              as a_FreeDom01,
a.FreeDom02              as a_FreeDom02,
a.FreeDom03              as a_FreeDom03,
a.FreeDom04              as a_FreeDom04,
a.FreeDom05              as a_FreeDom05,
a.FreeDom06              as a_FreeDom06,
a.FreeDom07              as a_FreeDom07,
a.FreeDom08              as a_FreeDom08,
a.FreeDom09              as a_FreeDom09,
a.FreeDom10              as a_FreeDom10,
a.FreeDom11              as a_FreeDom11,
a.FreeDom12              as a_FreeDom12,
a.FreeDom13              as a_FreeDom13,
a.FreeDom14              as a_FreeDom14,
a.FreeDom15              as a_FreeDom15,
a.FreeDom16              as a_FreeDom16,
a.FreeDomDateDif         as a_FreeDomDateDif,
a.Custom3                as a_Custom3,
a.Custom4                as a_Custom4,
a.SourceVchtype2         as a_SourceVchtype2,
a.SourceVchcode2         as a_SourceVchcode2,
a.SourceDlyOrder2        as a_SourceDlyOrder2,
a.BtypeOtherCode         as a_BtypeOtherCode,
a.ProduceDate            as a_ProduceDate,
a.IniInStockToQty        as a_IniInStockToQty,
a.IniInStockBackQty      as a_IniInStockBackQty,
a.InStockToQty           as a_InStockToQty,
a.InStockBackQty         as a_InStockBackQty,
a.InstockBackSign        as a_InstockBackSign,
a.IniInstockToQtyOther   as a_IniInstockToQtyOther,
a.IniToFactQty           as a_IniToFactQty,
a.BtypeOtherName         as a_BtypeOtherName,
a.BtypeOtherStandardType as a_BtypeOtherStandardType,
a.AuditInstockToQty      as a_AuditInstockToQty,
b.Vchcode                as b_Vchcode,
b.btypeid                as b_btypeid,
b.etypeid                as b_etypeid,
b.ktypeid                as b_ktypeid,
b.PtypeId                as b_PtypeId,
b.Qty                    as b_Qty,
b.Blockno                as b_Blockno,
b.Prodate                as b_Prodate,
b.comment                as b_comment,
b.date                   as b_date,
b.usedtype               as b_usedtype,
b.period                 as b_period,
b.Vchtype                as b_Vchtype,
b.dlyorder               as b_dlyorder,
b.unit                   as b_unit,
b.SourceDlyOrder         as b_SourceDlyOrder,
b.ktypeid2               as b_ktypeid2,
b.SourceVchType          as b_SourceVchType,
b.UserDefined01          as b_UserDefined01,
b.UserDefined02          as b_UserDefined02,
b.FreeDom01              as b_FreeDom01,
b.FreeDom02              as b_FreeDom02,
b.FreeDom03              as b_FreeDom03,
b.FreeDom04              as b_FreeDom04,
b.FreeDom05              as b_FreeDom05,
b.FreeDom06              as b_FreeDom06,
b.FreeDom07              as b_FreeDom07,
b.FreeDom08              as b_FreeDom08,
b.FreeDom09              as b_FreeDom09,
b.FreeDom10              as b_FreeDom10,
b.SourceVchcode          as b_SourceVchcode,
b.RowNo                  as b_RowNo,
b.Position               as b_Position,
b.Position2              as b_Position2,
b.Draft                  as b_Draft,
b.FreeDom11              as b_FreeDom11,
b.FreeDom12              as b_FreeDom12,
b.FreeDom13              as b_FreeDom13,
b.FreeDom14              as b_FreeDom14,
b.FreeDom15              as b_FreeDom15,
b.FreeDom16              as b_FreeDom16,
b.FreeDomDateDif         as b_FreeDomDateDif,
b.QtyOther               as b_QtyOther,
b.Custom3                as b_Custom3,
b.Custom4                as b_Custom4,
b.BtypeOtherCode         as b_BtypeOtherCode,
b.ProduceDate            as b_ProduceDate,
b.mtypeid                as b_mtypeid,
b.ParVchtype             as b_ParVchtype,
b.BtypeOtherName         as b_BtypeOtherName,
b.BtypeOtherStandardType as b_BtypeOtherStandardType,

-- 新增：A表字段的字典转译字段
bt1.FullName             as a_btypeid_fullname,       -- a.btypeid 对应的业务类型名称
e1.fullname              as a_etypeid_fullname,       -- a.etypeid 对应的员工名称
s1.fullname              as a_ktypeid_fullname,       -- a.ktypeid 对应的仓库名称
p1.fullname              as a_PtypeId_fullname,       -- a.PtypeId 对应的产品类型名称
case a.usedtype -- a.usedtype 转译
    when '1' then '主表格'
    when '2' then '钱流单等把表格外数据作为明细记录的表格'
    when '5' then '赠品'
    when '6' then '销售单抹零'
    when '7' then '次表格'
    else isnull(a.usedtype, '')
    end                  as a_usedtype_name,
v1.fullname              as a_Vchtype_fullname,       -- a.Vchtype 对应的单据类型名称
case a.RedOld -- a.RedOld 红冲标志转译
    when 'T' then
        case a.redword
            when 'T' then '红字单据'
            else '被红冲单据'
            end
    else ''
    end                  as a_RedOld_name,
d1.fullname              as a_ProjectID_fullname,     -- a.ProjectID 对应的部门名称
bt2.FullName             as a_SettleBtypeId_fullname, -- a.SettleBtypeId 对应的结算方名称
m1.fullname              as a_mtypeid_fullname,       -- a.mtypeid 对应的往来类型名称

-- 新增：B表字段的字典转译字段
bt3.FullName             as b_btypeid_fullname,       -- b.btypeid 对应的业务类型名称
e2.fullname              as b_etypeid_fullname,       -- b.etypeid 对应的员工名称
s2.fullname              as b_ktypeid_fullname,       -- b.ktypeid 对应的仓库名称
p2.fullname              as b_PtypeId_fullname,       -- b.PtypeId 对应的产品类型名称
v2.fullname              as b_Vchtype_fullname,       -- b.Vchtype 对应的单据类型名称
m2.fullname              as b_mtypeid_fullname,       -- b.mtypeid 对应的往来类型名称
case b.usedtype -- b.usedtype 转译
    when '1' then '主表格'
    when '2' then '钱流单等把表格外数据作为明细记录的表格'
    when '5' then '赠品'
    when '6' then '销售单抹零'
    when '7' then '次表格'
    else isnull(b.usedtype, '')
    end                  as b_usedtype_name

from DlyBuy as a
-- A表字典表左连接（不影响原结果集）
         left join btype as bt1 on a.btypeid = bt1.typeid
         left join Employee as e1 on a.etypeid = e1.typeId
         left join Stock as s1 on a.ktypeid = s1.typeId
         left join ptype as p1 on a.ptypeid = p1.typeId
         left join T_GBL_Vchtype as v1 on a.Vchtype = v1.Vchtype
         left join Department as d1 on a.ProjectID = d1.typeId
         left join Btype as bt2 on a.SettleBtypeId = bt2.typeId
         left join Mtype as m1 on a.mtypeid = m1.typeid

-- 原主SQL的Dlystock左连接（保留原有连接逻辑）
         left join Dlystock as b
                   on a.Vchcode = b.SourceVchcode
                       and a.Vchtype = b.SourceVchType
                       and a.dlyorder = b.SourceDlyOrder

-- B表字典表左连接（不影响原结果集）
         left join btype as bt3 on b.btypeid = bt3.typeid
         left join Employee as e2 on b.etypeid = e2.typeId
         left join Stock as s2 on b.ktypeid = s2.typeId
         left join ptype as p2 on b.ptypeid = p2.typeId
         left join T_GBL_Vchtype as v2 on b.Vchtype = v2.Vchtype
         left join Mtype as m2 on b.mtypeid = m2.typeid

where a.vchtype = '34'
--and (b.sourceVchcode is null or a.qty != b.qty);