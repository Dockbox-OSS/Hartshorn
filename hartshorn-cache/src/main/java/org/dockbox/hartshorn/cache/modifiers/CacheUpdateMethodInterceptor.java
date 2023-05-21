package org.dockbox.hartshorn.cache.modifiers;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.ApplicationException;

public class CacheUpdateMethodInterceptor<T, R> implements MethodInterceptor<T, R> {
    private final CacheContext cacheContext;
    private final ApplicationContext context;

    public CacheUpdateMethodInterceptor(final CacheContext cacheContext, final ApplicationContext context) {
        this.cacheContext = cacheContext;
        this.context = context;
    }

    @Override
    public R intercept(final MethodInterceptorContext<T, R> interceptorContext) throws Throwable {
        try {
            final Object o = interceptorContext.args()[0];
            this.cacheContext.manager().get(this.cacheContext.cacheName())
                    .peek(cache -> cache.put(this.cacheContext.key(), o));
            return interceptorContext.invokeDefault();
        }
        catch (final ApplicationException e) {
            this.context.handle(e);
            return interceptorContext.method().returnType().defaultOrNull();
        }
    }
}
