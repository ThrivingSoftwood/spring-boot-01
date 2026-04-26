// File: component/config/CommonAuthConfig.java
package thriving.softwood.common.auth.config;

import java.util.concurrent.TimeUnit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 不加改动时的鉴权配置
 */
@AutoConfiguration
@ComponentScan("thriving.softwood.common.auth")
@MapperScan("thriving.softwood.common.auth.infrastructure.db.master.mapper")
public class AncestorAuthConfig {

    public static final String AUTH_CACHE_KEY = "auth:";
    public static final String AUTH_CACHE_MANAGER = "authCacheManager";

    // 🌟 新增：用户权限高速缓存 (用于无感刷新和拦截器极速校验)
    public static final String USER_AUTH_INFO_CACHE = AUTH_CACHE_KEY + "userAuthInfoCache";

    // 🌟 将专属于 Auth 权限的 Caffeine 缓存管理器配置在此
    @Bean(AUTH_CACHE_MANAGER)
    public CacheManager authCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache(USER_AUTH_INFO_CACHE, Caffeine.newBuilder().initialCapacity(500)
            .maximumSize(5000).expireAfterWrite(60, TimeUnit.SECONDS).build());
        return cacheManager;
    }
}