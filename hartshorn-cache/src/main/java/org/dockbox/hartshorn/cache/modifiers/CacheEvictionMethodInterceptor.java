package org.dockbox.hartshorn.cache.modifiers;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.ApplicationException;

public class CacheEvictionMethodInterceptor<T, R> implements MethodInterceptor<T, R> {
    private final CacheContext cacheContext;
    private final ApplicationContext context;

    public CacheEvictionMethodInterceptor(final CacheContext cacheContext, final ApplicationContext context) {
        this.cacheContext = cacheContext;
        this.context = context;
    }

    @Override
    public R intercept(final MethodInterceptorContext<T, R> interceptorContext) throws Throwable {
        try {
            this.cacheContext.manager().get(this.cacheContext.cacheName()).peek(Cache::invalidate);
            return interceptorContext.invokeDefault();
        }
        catch (final ApplicationException e) {
            this.context.handle(e);
        }
        return null; // Should be void anyway
    }
}
