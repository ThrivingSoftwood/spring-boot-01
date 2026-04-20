package thriving.softwood.kaishi.component.config;

import static thriving.softwood.common.auth.config.AncestorAuthConfig.AUTH_CACHE_MANAGER;
import static thriving.softwood.kaishi.infrastructure.cache.local.KaishiCaffeineCacheConfig.KAISHI_CACHE_MANAGER;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;
import thriving.softwood.kaishi.component.resolver.SmartCacheResolver;

@Configuration
@EnableCaching
public class MultiCacheConfig extends CachingConfigurerSupport {

    @Resource(name = KAISHI_CACHE_MANAGER)
    private CacheManager kaishiCacheManager;

    @Resource(name = AUTH_CACHE_MANAGER)
    private CacheManager authCacheManager;

    /**
     * 注册我们自定义的解析器
     */
    @Bean
    public CacheResolver smartCacheResolver() {
        return new SmartCacheResolver(kaishiCacheManager, authCacheManager);
    }

    /**
     * 🌟 关键：重写此方法，让系统默认使用这个解析器 这样你在写 @Cacheable 时，如果不指定，就会走 smartCacheResolver 的路由逻辑
     */
    @Override
    public CacheResolver cacheResolver() {
        return smartCacheResolver();
    }
}