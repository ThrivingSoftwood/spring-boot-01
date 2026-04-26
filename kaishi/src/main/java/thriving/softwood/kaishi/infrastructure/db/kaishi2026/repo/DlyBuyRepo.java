package thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.core.util.ObjUtil;
import thriving.softwood.common.auth.pojo.record.FinishPurchaseReq;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.biz.pojo.dto.DlyBuyDTO;
import thriving.softwood.kaishi.biz.pojo.dto.PurchaseOrderTraceDTO;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.DlyBuy;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.extend.PurchaseTraceOrder;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.base.DlyBuyMapper;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.extend.DlyBuyExtendMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@DS("kaishi-2026")
@Service
public class DlyBuyRepo extends AncestorServiceImpl<DlyBuyMapper, DlyBuy> {
    private DlyBuyExtendMapper extendMapper;

    @Autowired
    public void setExtendMapper(DlyBuyExtendMapper extendMapper) {
        this.extendMapper = extendMapper;
    }

    public List<DlyBuy> list(PurchaseOrderTraceDTO dto) {
        LambdaQueryWrapper<DlyBuy> wrapper = new LambdaQueryWrapper<>();

        if (dto == null) {
            return List.of();
        }

        // ========== 内联非空判断（仅修正类型判断错误，逻辑完全不变） ==========
        wrapper.eq(ObjUtil.isNotEmpty(dto.getVchcode()), DlyBuy::getVchcode, dto.getVchcode())
            .eq(StrUtil.isNotBlank(dto.getAtypeid()), DlyBuy::getAtypeid, dto.getAtypeid())
            .eq(StrUtil.isNotBlank(dto.getBtypeid()), DlyBuy::getBtypeid, dto.getBtypeid())
            .eq(StrUtil.isNotBlank(dto.getEtypeid()), DlyBuy::getEtypeid, dto.getEtypeid())
            .eq(StrUtil.isNotBlank(dto.getKtypeid()), DlyBuy::getKtypeid, dto.getKtypeid())
            .eq(StrUtil.isNotBlank(dto.getPtypeId()), DlyBuy::getPtypeId, dto.getPtypeId())
            .eq(StrUtil.isNotBlank(dto.getBlockno()), DlyBuy::getBlockno, dto.getBlockno())
            .ge(StrUtil.isNotBlank(dto.getMinProdate()), DlyBuy::getProdate, dto.getMinProdate())
            .le(StrUtil.isNotBlank(dto.getMaxProdate()), DlyBuy::getProdate, dto.getMaxProdate())
            .like(StrUtil.isNotBlank(dto.getComment()), DlyBuy::getComment, dto.getComment())
            .ge(StrUtil.isNotBlank(dto.getMinDate()), DlyBuy::getDate, dto.getMinDate())
            .le(StrUtil.isNotBlank(dto.getMaxDate()), DlyBuy::getDate, dto.getMaxDate())
            .eq(StrUtil.isNotBlank(dto.getUsedtype()), DlyBuy::getUsedtype, dto.getUsedtype())
            .eq(ObjUtil.isNotEmpty(dto.getPeriod()), DlyBuy::getPeriod, dto.getPeriod())
            .eq(ObjUtil.isNotEmpty(dto.getVchtype()), DlyBuy::getVchtype, dto.getVchtype())
            .eq(ObjUtil.isNotEmpty(dto.getDlyorder()), DlyBuy::getDlyorder, dto.getDlyorder())
            .eq(ObjUtil.isNotEmpty(dto.getSourceDlyOrder()), DlyBuy::getSourceDlyOrder, dto.getSourceDlyOrder())
            .eq(ObjUtil.isNotEmpty(dto.getSourceVchcode()), DlyBuy::getSourceVchcode, dto.getSourceVchcode())
            .eq(ObjUtil.isNotEmpty(dto.getSourceVchtype()), DlyBuy::getSourceVchtype, dto.getSourceVchtype())
            .eq(StrUtil.isNotBlank(dto.getProjectId()), DlyBuy::getProjectID, dto.getProjectId())
            .eq(ObjUtil.isNotEmpty(dto.getDraft()), DlyBuy::getDraft, dto.getDraft())
            .eq(StrUtil.isNotBlank(dto.getSettleBtypeId()), DlyBuy::getSettleBtypeId, dto.getSettleBtypeId())
            .eq(StrUtil.isNotBlank(dto.getMtypeid()), DlyBuy::getMtypeid, dto.getMtypeid())
            .eq(ObjUtil.isNotEmpty(dto.getParVchtype()), DlyBuy::getParVchtype, dto.getParVchtype());

        return baseMapper.selectList(wrapper);
    }

    public Long countTraceInfo(PurchaseOrderTraceDTO dto) {
        return extendMapper.countTraceInfo(dto);
    }

    public List<DlyBuyDTO> listTraceInfo(PurchaseOrderTraceDTO dto) {
        return extendMapper.listTraceInfo(dto);
    }

    public List<PurchaseTraceOrder> listTraceDetail(PurchaseOrderTraceDTO dto) {
        return extendMapper.listTraceDetail(dto);
    }

    public Long count(FinishPurchaseReq dto) {
        LambdaQueryWrapper<DlyBuy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DlyBuy::getDlyorder, dto.dlyOrder()).eq(DlyBuy::getVchcode, dto.vchCode()).eq(DlyBuy::getVchtype,
            dto.vchType());
        return baseMapper.selectCount(wrapper);
    }
}
