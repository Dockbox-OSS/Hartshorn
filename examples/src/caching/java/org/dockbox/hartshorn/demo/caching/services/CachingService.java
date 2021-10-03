package org.dockbox.hartshorn.demo.caching.services;

import org.dockbox.hartshorn.boot.ServerState.Started;
import org.dockbox.hartshorn.cache.annotations.CacheService;
import org.dockbox.hartshorn.cache.annotations.Cached;
import org.dockbox.hartshorn.cache.annotations.EvictCache;
import org.dockbox.hartshorn.demo.caching.KeyUtility;
import org.dockbox.hartshorn.demo.caching.domain.User;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.events.EngineChangedState;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;

/**
 * A simple service capable of caching collections. If you only use automatic cache expiration through {@link Cached#expires()},
 * the service can also be annotated with {@link org.dockbox.hartshorn.di.annotations.service.Service}. However, if you want to
 * use {@link EvictCache} and/or {@link org.dockbox.hartshorn.cache.annotations.UpdateCache} as well, it is recommended to
 * annotate the service with {@link CacheService} to indicate the cache ID.
 */
@CacheService("user-cache")
public abstract class CachingService {

    @Cached
    public List<User> getUsers(final ApplicationContext context) {
        final User user = new User(context, "Guus", 21);

        user.set(KeyUtility.LAST_MODIFIED, System.nanoTime());
        return HartshornUtils.singletonList(user);
    }

    @EvictCache
    public abstract void evict();

    @Listener
    public void on(EngineChangedState<Started> event) {
        final User user = this.getUsers(event.applicationContext()).get(0);
        final long initial = user.get(KeyUtility.LAST_MODIFIED).or(-1L);

        final User user2 = this.getUsers(event.applicationContext()).get(0);
        final long beforeEviction = user2.get(KeyUtility.LAST_MODIFIED).or(-1L);

        long difference = beforeEviction - initial;
        event.applicationContext().log().info("(Before eviction) Users are the same: " + (user == user2) + ", modification time difference: " + difference);

        this.evict();

        final User user3 = this.getUsers(event.applicationContext()).get(0);
        final long afterEviction = user3.get(KeyUtility.LAST_MODIFIED).or(-1L);

        long evictedDifference = afterEviction - beforeEviction;
        event.applicationContext().log().info("(After eviction) Users are the same: " + (user2 == user3) + ", modification time difference: " + evictedDifference);
    }
}
