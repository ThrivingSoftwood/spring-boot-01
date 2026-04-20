package thriving.softwood.kaishi.infrastructure.cache.local;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * @author ThrivingSoftwood
 */
@Configuration
@EnableCaching // 🚀 极其关键：开启 Spring 缓存注解支持
public class KaishiCaffeineCacheConfig {

    public static final String KAISHI_CACHE_MANAGER = "kaishiCacheManager";

    // 定义字典缓存的名称常量，防止手写拼写错误
    public static final String KAISHI_CACHE_KEY = "kaishi:";
    public static final String BTYPE_CACHE = KAISHI_CACHE_KEY + "btypeCache";
    public static final String EMPLOYEE_CACHE = KAISHI_CACHE_KEY + "employeeCache";
    public static final String STOCK_CACHE = KAISHI_CACHE_KEY + "stockCache";
    public static final String PTYPE_CACHE = KAISHI_CACHE_KEY + "ptypeCache";
    public static final String VCHTYPE_CACHE = KAISHI_CACHE_KEY + "vchtypeCache";
    public static final String DEPARTMENT_CACHE = KAISHI_CACHE_KEY + "departmentCache";
    public static final String MTYPE_CACHE = KAISHI_CACHE_KEY + "mtypeCache";
    public static final String DLYNDX_CACHE = KAISHI_CACHE_KEY + "dlyndxCache";
    public static final String USEDTYPE_CACHE = KAISHI_CACHE_KEY + "usedtypeCache";
    public static final String RED_WORD_CACHE = KAISHI_CACHE_KEY + "redWordCache";
    public static final String PDETAIL_CACHE = KAISHI_CACHE_KEY + "pdetailCache";

    @Bean(name = KAISHI_CACHE_MANAGER)
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 针对不同类型的缓存进行精细化配置
        cacheManager.registerCustomCache(BTYPE_CACHE, caffeine(6642, 10000, 3, TimeUnit.HOURS).build());
        cacheManager.registerCustomCache(EMPLOYEE_CACHE, caffeine(181, 300, 3, TimeUnit.HOURS).build());
        cacheManager.registerCustomCache(STOCK_CACHE, caffeine(13, 40, 3, TimeUnit.HOURS).build());
        cacheManager.registerCustomCache(PTYPE_CACHE, caffeine(36102, 50000, 3, TimeUnit.HOURS).build());
        cacheManager.registerCustomCache(VCHTYPE_CACHE, caffeine(98, 150, 3, TimeUnit.HOURS).build());
        cacheManager.registerCustomCache(DEPARTMENT_CACHE, caffeine(8, 50, 3, TimeUnit.HOURS).build());
        cacheManager.registerCustomCache(MTYPE_CACHE, caffeine(71, 150, 3, TimeUnit.HOURS).build());
        cacheManager.registerCustomCache(DLYNDX_CACHE, caffeine(1000, 20000, 3, TimeUnit.HOURS).build());
        cacheManager.registerCustomCache(USEDTYPE_CACHE, caffeine(5, 5).build());
        cacheManager.registerCustomCache(RED_WORD_CACHE, caffeine(2, 2).build());
        cacheManager.registerCustomCache(PDETAIL_CACHE, caffeine(2, 2).build());

        return cacheManager;
    }

    private Caffeine<Object, Object> caffeine(int initialCapacity, int maximumSize) {
        return Caffeine.newBuilder().initialCapacity(initialCapacity).maximumSize(maximumSize);
    }

    private Caffeine<Object, Object> caffeine(int initialCapacity, int maximumSize, long expireAfterWrite,
        TimeUnit expireAfterWriteUnit) {
        return Caffeine.newBuilder().initialCapacity(initialCapacity).maximumSize(maximumSize)
            .expireAfterWrite(expireAfterWrite, expireAfterWriteUnit);
    }
}