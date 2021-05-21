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

package org.dockbox.selene.cache.modifiers;

import org.dockbox.selene.cache.context.CacheContext;
import org.dockbox.selene.cache.annotations.EvictCache;
import org.dockbox.selene.cache.context.CacheMethodContext;
import org.dockbox.selene.cache.context.SimpleCacheMethodContext;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.proxy.service.MethodProxyContext;

public class CacheEvictionMethodModifier extends CacheServiceModifier<EvictCache> {

    @Override
    protected <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext, CacheContext cacheContext) {
        return (instance, args, proxyContext) -> {
            final Object o = args[0];
            cacheContext.getManager().update(cacheContext.getName(), o);
            return null; // Should be void anyway
        };
    }

    @Override
    protected CacheMethodContext getContext(MethodProxyContext<?> context) {
        final EvictCache evict = context.getAnnotation(EvictCache.class);
        return new SimpleCacheMethodContext(evict.manager(), evict.value(), null);
    }

    @Override
    public Class<EvictCache> annotation() {
        return EvictCache.class;
    }
}
