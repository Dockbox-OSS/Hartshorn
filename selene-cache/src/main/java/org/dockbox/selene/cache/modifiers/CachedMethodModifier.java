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

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.cache.Cache;
import org.dockbox.selene.cache.Expiration;
import org.dockbox.selene.cache.annotations.Cached;
import org.dockbox.selene.cache.context.CacheContext;
import org.dockbox.selene.cache.context.CacheMethodContext;
import org.dockbox.selene.cache.context.SimpleCacheMethodContext;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.exceptions.ApplicationException;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.proxy.service.MethodProxyContext;
import org.dockbox.selene.util.Reflect;

import java.util.Collection;
import java.util.List;

public class CachedMethodModifier extends CacheServiceModifier<Cached> {

    @Override
    protected <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext, CacheContext cacheContext) {
        return (instance, args, proxyContext) -> {
            Cache<Object> cache = cacheContext.getCache();

            final Exceptional<Collection<Object>> content = cache.get();

            //noinspection unchecked
            return (R) content.then(() -> {
                try {
                    final List<Object> out = proxyContext.invoke();
                    cache.populate(out);
                    return out;
                } catch (ApplicationException e) {
                    Except.handle(e);
                    return null;
                }
            }).orNull(); // In case of void returns
        };
    }

    @Override
    protected CacheMethodContext getContext(MethodProxyContext<?> context) {
        final Cached cached = context.getAnnotation(Cached.class);
        return new SimpleCacheMethodContext(cached.manager(), cached.value(), Expiration.of(cached.expires()));
    }

    @Override
    public <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext) {
        return Reflect.assignableFrom(Collection.class, methodContext.getReturnType());
    }

    @Override
    public Class<Cached> annotation() {
        return Cached.class;
    }
}
