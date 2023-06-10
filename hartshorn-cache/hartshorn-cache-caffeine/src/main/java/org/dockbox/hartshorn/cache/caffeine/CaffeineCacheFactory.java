package org.dockbox.hartshorn.cache.caffeine;

import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.CacheFactory;
import org.dockbox.hartshorn.cache.Expiration;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;

@Service
@RequiresActivator(UseCaching.class)
public class CaffeineCacheFactory implements CacheFactory {

    @Override
    public <K, V> Cache<K, V> cache(final Expiration expiration) {
        return new CaffeineCache<>(expiration);
    }
}
