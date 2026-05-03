package thriving.softwood.kaishi.biz.runner;

import static thriving.softwood.common.core.constant.LetterConstant.UPPER_F;
import static thriving.softwood.common.core.constant.LetterConstant.UPPER_T;
import static thriving.softwood.kaishi.infrastructure.cache.local.KaishiCaffeineCacheConfig.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import thriving.softwood.common.security.util.SecurityUtil;
import thriving.softwood.kaishi.biz.pojo.dto.DlyndxDTO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangProducts;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangProductsRepo;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.*;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo.*;

/**
 * 字典数据全量预热器
 * 
 * 在 Spring Boot 启动完成后自动执行，将 DB 数据全量灌入 Caffeine 内存。
 * 
 * @author ThrivingSoftwood
 */
@Component
public class DictionaryPreloadRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DictionaryPreloadRunner.class);

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

    @Autowired
    public DictionaryPreloadRunner(@Qualifier(KAISHI_CACHE_MANAGER) CacheManager cacheManager,
        DepartmentRepo departmentRepo, BtypeRepo btypeRepo, EmployeeRepo employeeRepo, MtypeRepo mtypeRepo,
        PtypeRepo ptypeRepo, StockRepo stockRepo, GblVchtypeRepo gblVchtypeRepo, DlyndxRepo dlyndxRepo,
        EdongfangProductsRepo edongfangProductsRepo) {
        this.cacheManager = cacheManager;
        this.departmentRepo = departmentRepo;
        this.btypeRepo = btypeRepo;
        this.employeeRepo = employeeRepo;
        this.mtypeRepo = mtypeRepo;
        this.ptypeRepo = ptypeRepo;
        this.stockRepo = stockRepo;
        this.gblVchtypeRepo = gblVchtypeRepo;
        this.dlyndxRepo = dlyndxRepo;
        this.edongfangProductsRepo = edongfangProductsRepo;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("🚀 开始进行字典数据全量预热 (Cache Pre-loading)...");

        // 🌟 使用工具类提权执行，这样内部所有的 Repo 查库 SQL 都会被拦截器直接放行
        SecurityUtil.runAsSystem(() -> {
            long start = System.currentTimeMillis();

            // 预热 btype、SettleBtypeId
            Cache btypeCache = cacheManager.getCache(BTYPE_CACHE);
            List<Btype> btypes = btypeRepo.ListAllTypeidAndFullname();
            for (Btype btype : btypes) {
                btypeCache.put(btype.getTypeId(), btype.getFullName());
            }

            // 预热 Employee
            Cache employeeCache = cacheManager.getCache(EMPLOYEE_CACHE);
            List<Employee> employees = employeeRepo.ListAllTypeidAndFullname();
            for (Employee obj : employees) {
                employeeCache.put(obj.getTypeId(), obj.getFullName());
            }

            // 预热 Stock 库存
            Cache stockCache = cacheManager.getCache(STOCK_CACHE);
            List<Stock> stocks = stockRepo.ListAllTypeidAndFullname();
            for (Stock obj : stocks) {
                stockCache.put(obj.getTypeId(), obj.getFullName());
            }

            // 预热 ptype
            Cache ptypeCache = cacheManager.getCache(PTYPE_CACHE);
            List<Ptype> ptypes = ptypeRepo.ListAllTypeidAndFullname();
            for (Ptype obj : ptypes) {
                ptypeCache.put(obj.getTypeId(), obj.getFullName());
            }

            // 预热 vchtype 业务类型
            Cache vchtypeCache = cacheManager.getCache(VCHTYPE_CACHE);
            List<GblVchtype> vchtypes = gblVchtypeRepo.ListAllTypeidAndFullname();
            for (GblVchtype obj : vchtypes) {
                vchtypeCache.put(obj.getVchtype(), obj.getFullname());
            }

            // 预热 department 部门
            Cache departmentCache = cacheManager.getCache(DEPARTMENT_CACHE);
            List<Department> departments = departmentRepo.ListAllTypeidAndFullname();
            for (Department obj : departments) {
                departmentCache.put(obj.getTypeid(), obj.getFullName());
            }

            // 预热 ptype 发票类型
            Cache mtypeCache = cacheManager.getCache(MTYPE_CACHE);
            List<Mtype> mtypes = mtypeRepo.ListAllTypeidAndFullname();
            for (Mtype obj : mtypes) {
                mtypeCache.put(obj.getTypeid(), obj.getFullName());
            }

            // 预热 dlyndx 订单信息
            Cache dlyndxCache = cacheManager.getCache(DLYNDX_CACHE);
            List<Dlyndx> dlyndxes = dlyndxRepo.listAllByVchcode();
            for (Dlyndx obj : dlyndxes) {
                dlyndxCache.put(obj.getVchcode(), new DlyndxDTO(obj));
            }

            // 预热 dlyndx 订单信息
            Cache edongfangProductNameCache = cacheManager.getCache(EDONGFANG_PRODUCT_NAME_CACHE);
            List<EdongfangProducts> edongfangProducts = edongfangProductsRepo.list();
            for (EdongfangProducts obj : edongfangProducts) {
                edongfangProductNameCache.put(obj.getSku(), obj.getName());
            }

            // USEDTYPE_CACHE
            // RED_WORD_CACHE
            // PDETAIL_CACHE

            // 预热表格类型
            Cache usedtypeCache = cacheManager.getCache(USEDTYPE_CACHE);
            usedtypeCache.put("1", "主表格");
            usedtypeCache.put("2", "钱流单等把表格外数据作为明细记录的表格");
            usedtypeCache.put("5", "赠品");
            usedtypeCache.put("6", "销售单抹零");
            usedtypeCache.put("7", "'次表格'");

            // 红冲标记
            Cache redWordCache = cacheManager.getCache(RED_WORD_CACHE);
            redWordCache.put(UPPER_T, "是");
            redWordCache.put(UPPER_F, "否");

            // 库存类型
            Cache pdetailCache = cacheManager.getCache(PDETAIL_CACHE);
            pdetailCache.put(0, "实物库存");
            pdetailCache.put(1, "账面库存");

            // ... 继续预热其他字典 ...

            long end = System.currentTimeMillis();
            log.info("✅ 字典数据预热完成！耗时: {} ms，系统准备就绪，可以接收请求了。", (end - start));
        });
    }
}