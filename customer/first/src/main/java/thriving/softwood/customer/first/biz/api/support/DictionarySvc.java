package thriving.softwood.customer.first.biz.api.support;

import static thriving.softwood.customer.first.infrastructure.cache.local.CaffeineCacheConfig.KAISHI_CACHE_MANAGER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

;

@Service
public class DictionarySvc implements DictionaryApi {

    private final CacheManager cacheManager;

    @Autowired
    public DictionarySvc(@Qualifier(KAISHI_CACHE_MANAGER) CacheManager cachemanager) {
        cacheManager = cachemanager;
    }
}
