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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.Expiration;
import org.dockbox.hartshorn.cache.annotations.Cached;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContextImpl;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.handle.ProxyFunction;
import org.dockbox.hartshorn.proxy.service.MethodProxyContext;

import java.util.Collection;
import java.util.List;

/**
 * The {@link org.dockbox.hartshorn.proxy.service.ServiceAnnotatedMethodModifier} responsible for {@link Cached}
 * decorated methods. This delegates functionality to the underlying {@link org.dockbox.hartshorn.cache.CacheManager}
 * to store or obtain {@link Cache} entries.
 */
public class CachedMethodModifier extends CacheServiceModifier<Cached> {

    @Override
    protected <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final CacheContext cacheContext) {
        return (instance, args, proxyContext) -> {
            final Cache<Object> cache = cacheContext.cache();

            final Exceptional<Collection<Object>> content = cache.get();

            //noinspection unchecked
            return (R) content.orElse(() -> {
                try {
                    final List<Object> out = proxyContext.invoke();
                    cache.populate(out);
                    return out;
                }
                catch (final ApplicationException e) {
                    Except.handle(e);
                    return null;
                }
            }).orNull(); // In case of void returns
        };
    }

    @Override
    protected CacheMethodContext context(final MethodProxyContext<?> context) {
        final Cached cached = context.annotation(Cached.class);
        return new CacheMethodContextImpl(cached.manager(), cached.value(), Expiration.of(cached.expires()));
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return methodContext.method().returnType().childOf(Collection.class);
    }

    @Override
    public Class<Cached> annotation() {
        return Cached.class;
    }
}
