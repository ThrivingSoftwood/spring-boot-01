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

    // 定义字典缓存的名称常量，防止手写拼写错误
    public static final String BTYPE_CACHE = "btypeCache";
    public static final String EMPLOYEE_CACHE = "employeeCache";
    public static final String STOCK_CACHE = "stockCache";
    public static final String PTYPE_CACHE = "ptypeCache";
    public static final String VCHTYPE_CACHE = "vchtypeCache";
    public static final String DEPARTMENT_CACHE = "departmentCache";
    public static final String MTYPE_CACHE = "mtypeCache";
    public static final String DLYNDX_CACHE = "dlyndxCache";
    public static final String USEDTYPE_CACHE = "usedtypeCache";
    public static final String RED_WORD_CACHE = "redWordCache";
    public static final String PDETAIL_CACHE = "pdetailCache";

    // 🌟 新增：用户权限高速缓存 (用于无感刷新和拦截器极速校验)
    public static final String USER_AUTH_INFO_CACHE = "userAuthInfoCache";

    @Bean
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

        // 🌟 新增：权限缓存，初始 500，最大在线 5000，TTL 60 秒 (过期极快，保证权限变更的实时性)
        cacheManager.registerCustomCache(USER_AUTH_INFO_CACHE, caffeine(500, 5000, 60, TimeUnit.SECONDS).build());

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