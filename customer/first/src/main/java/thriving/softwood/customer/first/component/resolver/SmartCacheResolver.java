package thriving.softwood.customer.first.component.resolver;

import static thriving.softwood.common.auth.config.AncestorAuthConfig.AUTH_CACHE_KEY;
import static thriving.softwood.customer.first.infrastructure.cache.local.KaishiCaffeineCacheConfig.KAISHI_CACHE_KEY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

/**
 * 🚀 智能缓存路由解析器 作用：根据缓存名称前缀，自动选择对应的 CacheManager
 */
public class SmartCacheResolver implements CacheResolver {

    private final CacheManager kaishiCacheManager;
    private final CacheManager authCacheManager;

    public SmartCacheResolver(CacheManager kaishiCacheManager, CacheManager authCacheManager) {
        this.kaishiCacheManager = kaishiCacheManager;
        this.authCacheManager = authCacheManager;
    }

    @Override
    @NonNull
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        // 1. 获取当前注解上定义的缓存名称集合 (如 @Cacheable(cacheNames = {"auth:user"}))
        Collection<String> cacheNames = context.getOperation().getCacheNames();
        List<Cache> result = new ArrayList<>();

        for (String name : cacheNames) {
            Cache cache;
            // 2. 🌟 路由逻辑：如果名字以 auth: 开头，去 auth 管理器找
            switch (name) {
                case AUTH_CACHE_KEY:
                    cache = authCacheManager.getCache(name);
                    break;
                case KAISHI_CACHE_KEY:
                default:
                    cache = kaishiCacheManager.getCache(name);
            }

            if (cache != null) {
                result.add(cache);
            }
        }
        return result;
    }
}