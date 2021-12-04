/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.cache.modifiers;

import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.Expiration;
import org.dockbox.hartshorn.cache.annotations.CacheService;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.cache.context.CacheContextImpl;
import org.dockbox.hartshorn.cache.context.CacheMethodContext;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.core.services.ServiceAnnotatedMethodPostProcessor;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Common functionality for cache related {@link ServiceAnnotatedMethodPostProcessor modifiers}.
 *
 * @param <A>
 *         The cache-related annotation
 */
public abstract class CacheServicePostProcessor<A extends Annotation> extends ServiceAnnotatedMethodPostProcessor<A, UseCaching> {

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        final CacheMethodContext cacheMethodContext = this.context(methodContext);
        final CacheManager manager = context.get(CacheManager.class);
        String name = cacheMethodContext.name();
        if ("".equals(name)) {
            final Exceptional<CacheService> annotation = methodContext.type().annotation(CacheService.class);
            if (annotation.present()) {
                name = annotation.get().value();
            }
            else {
                name = ComponentContainer.id(context, methodContext.type());
            }
        }

        final Expiration expiration = cacheMethodContext.expiration();
        final String finalName = name;

        context.log().debug("Determined cache name '" + finalName + "' for " + methodContext.method().qualifiedName());

        final Supplier<Cache<?>> cacheSupplier = () -> {
            final Cache<Object> cache;
            if (expiration != null)
                cache = manager.getOrCreate(finalName, expiration);
            else {
                cache = manager.get(finalName).orThrow(() -> new IllegalStateException("Requested state '" + finalName + "' has not been initialized"));
            }
            return cache;
        };

        final CacheContext cacheContext = new CacheContextImpl(manager, cacheSupplier, name);

        return this.process(context, methodContext, cacheContext);
    }

    protected abstract CacheMethodContext context(MethodProxyContext<?> context);

    protected abstract <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext, CacheContext cacheContext);

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return true;
    }

    @Override
    public Class<UseCaching> activator() {
        return UseCaching.class;
    }
}
