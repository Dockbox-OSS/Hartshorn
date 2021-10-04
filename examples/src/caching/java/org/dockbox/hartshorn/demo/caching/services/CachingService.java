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

    /**
     * Gets a singleton list containing a single {@link User}. The user is always unique, and is modified with
     * a {@link org.dockbox.hartshorn.api.keys.Key} indicating the time (in nanoseconds) when the user was last
     * modified.
     *
     * <p>This method is cached, and will return the same value until the cache is evicted using {@link #evict()}.
     */
    @Cached
    public List<User> getUsers(final ApplicationContext context) {
        final User user = new User(context, "Guus", 21);

        user.set(KeyUtility.LAST_MODIFIED, System.nanoTime());
        return HartshornUtils.singletonList(user);
    }

    /**
     * Evicts the active cache, making it so {@link #getUsers(ApplicationContext)} will return a new value.
     */
    @EvictCache
    public abstract void evict();

    /**
     * The method activated when the engine is done starting, this is done automatically when the application
     * was bootstrapped through {@link org.dockbox.hartshorn.boot.HartshornApplication}.
     *
     * <p>This example showcases the behavior of {@link #getUsers(ApplicationContext)} by comparing the
     * {@link KeyUtility#LAST_MODIFIED} {@link org.dockbox.hartshorn.api.keys.Key} on the first {@link User} in the
     * {@link List} returned by {@link #getUsers(ApplicationContext)}.
     *
     * <p>Note the use of the generic type parameter {@link Started} in the event. This causes this method to
     * activate only when a {@link EngineChangedState} event is posted with this exact type parameter. When the
     * posted parameter is another sub-class of {@link org.dockbox.hartshorn.boot.ServerState} this method will not
     * activate. However, if the notation of this event changed to {@code EngineChangedState<?>} it would activate
     * with any type parameter, as long as the event itself is a {@link EngineChangedState}.v
     */
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
