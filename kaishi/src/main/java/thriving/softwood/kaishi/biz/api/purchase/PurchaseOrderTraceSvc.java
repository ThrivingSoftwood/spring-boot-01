package thriving.softwood.kaishi.biz.api.purchase;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.util.ObjUtil;
import thriving.softwood.common.auth.pojo.record.FinishPurchaseReq;
import thriving.softwood.common.core.pojo.dto.SortItemDTO;
import thriving.softwood.common.core.util.SqlSecurityUtil;
import thriving.softwood.kaishi.biz.api.support.DictionaryApi;
import thriving.softwood.kaishi.biz.pojo.dto.DlyBuyDTO;
import thriving.softwood.kaishi.biz.pojo.dto.DlyndxDTO;
import thriving.softwood.kaishi.biz.pojo.dto.PurchaseOrderTraceDTO;
import thriving.softwood.kaishi.biz.pojo.vo.DlyBuyVO;
import thriving.softwood.kaishi.biz.pojo.vo.PurchaseTraceVO;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.extend.PurchaseTraceOrder;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo.DlyBuyRepo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.entity.base.PurchaseManualFinish;
import thriving.softwood.kaishi.infrastructure.db.ksplus.repo.PurchaseManualFinishRepo;

/**
 * @author ThrivingSoftwood
 */
@Service
public class PurchaseOrderTraceSvc implements PurchaseOrderTraceApi {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderTraceSvc.class);

    private final DlyBuyRepo dlyBuyRepo;
    private final DictionaryApi dict;
    private final PurchaseManualFinishRepo purchaseManualFinishRepo;

    @Autowired
    public PurchaseOrderTraceSvc(DlyBuyRepo dlyBuyRepo, DictionaryApi dictionaryApi,
        PurchaseManualFinishRepo purchaseManualFinishRepo) {
        this.dlyBuyRepo = dlyBuyRepo;
        dict = dictionaryApi;
        this.purchaseManualFinishRepo = purchaseManualFinishRepo;
    }

    @Override
    public List<DlyBuyVO> listTraceInfo(PurchaseOrderTraceDTO dto) {
        // step 00 : 定义返回值
        List<DlyBuyVO> resultList = new ArrayList<>();
        // step 01 : 入参的空值处理
        predeal(dto);
        // step 02 : 查出目标数据
        Long total = dlyBuyRepo.countTraceInfo(dto);
        List<DlyBuyDTO> list = dlyBuyRepo.listTraceInfo(dto);
        // step 03 : 遍历拼装
        for (DlyBuyDTO obj : list) {
            // step 03-01 : 初始化 vo 对象
            DlyBuyVO vo = new DlyBuyVO(obj);
            vo.setTotal(total);
            // step 03-02 : 装载进销存索引数据
            loadNdxInfo(obj, vo);

            // step 03-03 : 装载采购单表字典数据
            loadDlyBuyDictName(obj, vo);
            // step 03-04 : 放入返回值中
            resultList.add(vo);
        }
        return resultList;
    }

    private void loadDlyBuyDictName(DlyBuyDTO dto, DlyBuyVO vo) {
        DlyndxDTO dlyndxDTO = dict.getDlyndxDTO(dto.getVchCode());
        vo.setNumber(dlyndxDTO.getNumber());
        vo.setSummary(dlyndxDTO.getSummary());
        vo.setBtypeFullname(dict.getBtypeName(dto.getBtypeId()));
        vo.setEtypeFullname(dict.getEmployeeName(dto.getEtypeId()));
        vo.setPtypeFullname(dict.getPtypeName(dto.getPtypeId()));
        vo.setRedWord(dict.getRedWordName(dto.getRedWord()));
        vo.setRedOld(dict.getRedWordName(dto.getRedOld()));
    }

    private void loadNdxInfo(DlyBuyDTO dto, DlyBuyVO vo) {
        DlyndxDTO dlyndxDTO = dict.getDlyndxDTO(dto.getVchCode());
        vo.setNumber(dlyndxDTO.getNumber());
        vo.setSummary(dlyndxDTO.getSummary());
    }

    @Override
    public List<PurchaseTraceVO> listTraceDetail(PurchaseOrderTraceDTO dto) {
        // step 00 : 定义返回值
        List<PurchaseTraceVO> resultList = new ArrayList<>();
        // step 01 : 入参的空值处理
        predeal(dto);
        // step 02 : 查出目标数据
        List<PurchaseTraceOrder> list = dlyBuyRepo.listTraceDetail(dto);
        // step 03 : 遍历拼装
        for (PurchaseTraceOrder obj : list) {
            // step 03-01 : 装载进销存索引数据
            loadNdxInfo(obj);
            // step 03-02 : 装载采购单表字典数据
            loadDlyBuyDictName(obj);
            // step 03-03 : 装载入库单表数据
            if (ObjUtil.isNotEmpty(obj.getBVchcode())) {
                loadDlystockDictName(obj);
            }
            // step 03-04 : 放入返回值中
            resultList.add(new PurchaseTraceVO(obj));
        }
        return resultList;
    }

    private void loadDlystockDictName(PurchaseTraceOrder obj) {
        obj.setBBtypeidFullname(dict.getBtypeName(obj.getBBtypeid()));
        obj.setBEtypeidFullname(dict.getEmployeeName(obj.getBEtypeid()));
        obj.setBKtypeidFullname(dict.getStockName(obj.getBKtypeid()));
        obj.setBPtypeIdFullname(dict.getPtypeName(obj.getBPtypeId()));
        obj.setBVchtypeFullname(dict.getVchName(obj.getBVchtype()));
        obj.setBMtypeidFullname(dict.getMtypeName(obj.getBMtypeid()));
        obj.setBUsedtypeName(dict.getUsedtypeName(obj.getAUsedtype()));
    }

    private void loadDlyBuyDictName(PurchaseTraceOrder obj) {
        obj.setABtypeidFullname(dict.getBtypeName(obj.getABtypeid()));
        obj.setAEtypeidFullname(dict.getEmployeeName(obj.getAEtypeid()));
        obj.setAKtypeidFullname(dict.getStockName(obj.getAKtypeid()));
        obj.setAPtypeIdFullname(dict.getPtypeName(obj.getAPtypeId()));
        obj.setAVchtypeFullname(dict.getVchName(obj.getAVchtype()));
        obj.setAProjectIDFullname(dict.getDepartmentName(obj.getAProjectID()));
        obj.setASettleBtypeIdFullname(dict.getBtypeName(obj.getASettleBtypeId()));
        obj.setAMtypeidFullname(dict.getMtypeName(obj.getAMtypeid()));

        obj.setAUsedtypeName(dict.getUsedtypeName(obj.getAUsedtype()));
        obj.setARedwordName(dict.getRedWordName(obj.getARedword()));
        obj.setAPdetailName(dict.getPdetailName(obj.getAPdetail()));
        obj.setARedOldName(dict.getRedWordName(obj.getARedOld()));
    }

    private void loadNdxInfo(PurchaseTraceOrder obj) {
        DlyndxDTO dlyndxDTO = dict.getDlyndxDTO(obj.getAVchcode());
        obj.setNumber(dlyndxDTO.getNumber());
        obj.setSummary(dlyndxDTO.getSummary());
    }

    /**
     * 处理入参
     * 
     * @param dto 入参 dto
     */
    private void predeal(PurchaseOrderTraceDTO dto) {
        if (null == dto) {
            dto = new PurchaseOrderTraceDTO();
        }
        if (null == dto.getQueryPurchased()) {
            dto.setQueryPurchased(false);
        }
        if (null == dto.getPageNo() || dto.getPageNo() < 1L) {
            dto.setPageNo(1L);
        }
        if (null == dto.getPageSize() || dto.getPageSize() < 1L) {
            dto.setPageSize(50L);
        }
        dto.setOffset((dto.getPageNo() - 1L) * dto.getPageSize());
        // 设置默认值,然后根据 sortInfo 重新装填
        dto.setOrderBySql("a.vchcode asc, a.dlyorder asc");
        if (CollUtil.isNotEmpty(dto.getSortInfo())) {
            StringBuilder orderBySql = new StringBuilder();
            for (SortItemDTO sortItem : dto.getSortInfo()) {
                // 检查不通过直接抛异常
                SqlSecurityUtil.checkSortField(sortItem.getField());
                orderBySql.append(sortItem.getField()).append(" ").append(sortItem.getFlag() ? "asc" : "desc")
                    .append(",");
            }
            orderBySql.deleteCharAt(orderBySql.length() - 1);
            dto.setOrderBySql(orderBySql.toString());
        }

    }

    @Override
    public void manualFinishPurchase(FinishPurchaseReq dto) {
        if (0 == dlyBuyRepo.count(dto)) {
            throw new RuntimeException("申请无对应采购单!");
        }
        PurchaseManualFinish obj = new PurchaseManualFinish();
        obj.setDlyOrder(dto.dlyOrder());
        obj.setVchType(dto.vchType());
        obj.setVchCode(dto.vchCode());
        obj.setExtInfo(dto.extInfo());
        purchaseManualFinishRepo.save(obj);
    }
}
