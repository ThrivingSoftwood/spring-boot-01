package thriving.softwood.kaishi.biz.api.support;

import static thriving.softwood.common.core.constant.LetterConstant.UPPER_T;
import static thriving.softwood.kaishi.infrastructure.cache.local.KaishiCaffeineCacheConfig.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import thriving.softwood.kaishi.biz.pojo.dto.DlyndxDTO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangProductsRepo;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.extend.DictionaryExtendMapper;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo.*;

;

@Service
public class DictionarySvc implements DictionaryApi {

    private final CacheManager cacheManager;
    private final DepartmentRepo departmentRepo;
    private final BtypeRepo btypeRepo;
    private final EmployeeRepo employeeRepo;
    private final MtypeRepo mtypeRepo;
    private final PtypeRepo ptypeRepo;
    private final StockRepo stockRepo;
    private final GblVchtypeRepo gblVchtypeRepo;
    private final DlyndxRepo dlyndxRepo;
    private final EdongfangProductsRepo edongfangProductsRepo;
    private final DictionaryExtendMapper dictMapper;

    private Cache usedTypeCache;
    private Cache pdetailCache;
    private Cache redWordCache;

    @Autowired
    public DictionarySvc(@Qualifier(KAISHI_CACHE_MANAGER) CacheManager cachemanager, DepartmentRepo departmentRepo,
        BtypeRepo btypeRepo, EmployeeRepo employeeRepo, MtypeRepo mtypeRepo, PtypeRepo ptypeRepo, StockRepo stockRepo,
        GblVchtypeRepo gblVchtypeRepo, DlyndxRepo dlyndxRepo, EdongfangProductsRepo edongfangProductsRepo,
        DictionaryExtendMapper dictMapper) {
        cacheManager = cachemanager;
        this.departmentRepo = departmentRepo;
        this.btypeRepo = btypeRepo;
        this.employeeRepo = employeeRepo;
        this.mtypeRepo = mtypeRepo;
        this.ptypeRepo = ptypeRepo;
        this.stockRepo = stockRepo;
        this.gblVchtypeRepo = gblVchtypeRepo;
        this.dlyndxRepo = dlyndxRepo;
        this.edongfangProductsRepo = edongfangProductsRepo;
        usedTypeCache = cachemanager.getCache(USEDTYPE_CACHE);
        pdetailCache = cachemanager.getCache(PDETAIL_CACHE);
        redWordCache = cachemanager.getCache(RED_WORD_CACHE);
        this.dictMapper = dictMapper;
    }

    @Override
    @Cacheable(cacheNames = BTYPE_CACHE, key = "#typeId")
    public String getBtypeName(String typeId) {
        return dictMapper.getBtypeFullNameByTypeid(typeId);
    }

    @Override
    @Cacheable(cacheNames = EMPLOYEE_CACHE, key = "#typeId")
    public String getEmployeeName(String typeId) {
        return dictMapper.getEmployeeFullNameByTypeid(typeId);
    }

    @Override
    @Cacheable(cacheNames = STOCK_CACHE, key = "#typeId")
    public String getStockName(String typeId) {
        return dictMapper.getStockFullNameByTypeid(typeId);
    }

    @Override
    @Cacheable(cacheNames = PTYPE_CACHE, key = "#typeId")
    public String getPtypeName(String typeId) {
        return dictMapper.getPtypeFullNameByTypeid(typeId);
    }

    @Override
    @Cacheable(cacheNames = VCHTYPE_CACHE, key = "#vchtype")
    public String getVchName(Integer vchtype) {
        return dictMapper.getVchNameByVchtype(vchtype);
    }

    @Override
    @Cacheable(cacheNames = DEPARTMENT_CACHE, key = "#typeId")
    public String getDepartmentName(String typeId) {
        return dictMapper.getDepartmentFullNameByTypeid(typeId);
    }

    @Override
    @Cacheable(cacheNames = MTYPE_CACHE, key = "#typeId")
    public String getMtypeName(String typeId) {
        return dictMapper.getMtypeFullNameByTypeid(typeId);
    }

    @Override
    @Cacheable(cacheNames = DLYNDX_CACHE, key = "#vchcode")
    public DlyndxDTO getDlyndxDTO(Long vchcode) {
        return dictMapper.getDlyndxDTOByVchcode(vchcode);
    }

    @Override
    @Cacheable(cacheNames = EDONGFANG_PRODUCT_NAME_CACHE, key = "#sku")
    public String getEdongfangProductName(String sku) {
        return edongfangProductsRepo.getProductNameBySku(sku);
    }

    @Override
    public String getUsedtypeName(String code) {
        return usedTypeCache.get(code, String.class);
    }

    @Override
    public String getPdetailName(Integer code) {
        return pdetailCache.get(code, String.class);
    }

    @Override
    public String getRedWordName(String code) {
        return redWordCache.get(code, String.class);
    }

    @Override
    public String getRedOldName(String redOld, String redWord) {
        if (!UPPER_T.equals(redOld)) {
            return null;
        }
        return UPPER_T.equals(redWord) ? "红字单据" : "被红冲单据";
    }
}
