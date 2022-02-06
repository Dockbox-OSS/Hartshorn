package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;

import javax.inject.Singleton;

@Service(activators = UseCaching.class)
public class CacheProviders {

    @Provider
    public Class<? extends Cache> cache() {
        return CacheImpl.class;
    }

    @Provider
    @Singleton
    public Class<? extends CacheManager> cacheManager() {
        return CacheManagerImpl.class;
    }
}
