package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.cache.annotations.CacheService;
import org.dockbox.hartshorn.cache.annotations.EvictCache;
import org.dockbox.hartshorn.cache.annotations.UpdateCache;

@CacheService("non-abstract")
public class NonAbstractCacheService {

    @UpdateCache
    public long update(long s) {
        return s*2;
    }

    @EvictCache
    public boolean evict() {
        return true;
    }

}
