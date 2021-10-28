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

import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.cache.annotations.EvictCache;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContextImpl;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.services.ServiceAnnotatedMethodModifier;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.context.MethodProxyContext;

/**
 * The {@link ServiceAnnotatedMethodModifier} responsible for {@link EvictCache}
 * decorated methods. This delegates functionality to the underlying {@link org.dockbox.hartshorn.cache.CacheManager}
 * to evict specific {@link org.dockbox.hartshorn.cache.Cache caches}.
 */
public class CacheEvictionMethodModifier extends CacheServiceModifier<EvictCache> {

    @Override
    protected CacheMethodContext context(final MethodProxyContext<?> context) {
        final EvictCache evict = context.annotation(EvictCache.class);
        return new CacheMethodContextImpl(evict.value(), null);
    }

    @Override
    protected <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final CacheContext cacheContext) {
        return (instance, args, proxyContext) -> {
            try {
                cacheContext.manager().evict(cacheContext.name());
                return proxyContext.invoke(args);
            } catch (final ApplicationException e) {
                Except.handle(e);
            }
            return null; // Should be void anyway
        };
    }

    @Override
    public Class<EvictCache> annotation() {
        return EvictCache.class;
    }
}
