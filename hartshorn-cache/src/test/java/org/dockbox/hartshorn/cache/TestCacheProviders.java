package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Provider;

import jakarta.inject.Singleton;

@Service
@RequiresActivator(UseCaching.class)
public class TestCacheProviders {

    @Provider(priority = 0)
    @Singleton
    public Class<? extends CacheManager> cacheManager = JUnitCacheManager.class;

}
