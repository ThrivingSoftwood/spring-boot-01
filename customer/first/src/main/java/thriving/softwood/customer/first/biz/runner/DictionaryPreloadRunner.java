package thriving.softwood.customer.first.biz.runner;

import static thriving.softwood.common.core.constant.LetterConstant.UPPER_F;
import static thriving.softwood.common.core.constant.LetterConstant.UPPER_T;
import static thriving.softwood.customer.first.infrastructure.cache.local.CaffeineCacheConfig.*;

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

    @Autowired
    public DictionaryPreloadRunner(@Qualifier(KAISHI_CACHE_MANAGER) CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("🚀 开始进行字典数据全量预热 (Cache Pre-loading)...");

        // 🌟 使用工具类提权执行，这样内部所有的 Repo 查库 SQL 都会被拦截器直接放行
        SecurityUtil.runAsSystem(() -> {
            long start = System.currentTimeMillis();

            // // 预热 Employee
            // Cache employeeCache = cacheManager.getCache(EMPLOYEE_CACHE);
            // List<Employee> employees = employeeRepo.ListAllTypeidAndFullname();
            // for (Employee obj : employees) {
            // employeeCache.put(obj.getTypeId(), obj.getFullName());
            // }

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