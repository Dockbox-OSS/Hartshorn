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

import org.dockbox.selene.cache.Cache;
import org.dockbox.selene.cache.CacheManager;
import org.dockbox.selene.cache.Expiration;
import org.dockbox.selene.cache.annotations.CacheService;
import org.dockbox.selene.cache.annotations.UseCaching;
import org.dockbox.selene.cache.context.CacheContext;
import org.dockbox.selene.cache.context.CacheMethodContext;
import org.dockbox.selene.cache.context.SimpleCacheContext;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.proxy.service.MethodProxyContext;
import org.dockbox.selene.proxy.service.ServiceAnnotatedMethodModifier;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

public abstract class CacheServiceModifier<A extends Annotation> implements ServiceAnnotatedMethodModifier<A, UseCaching> {

    @Override
    public <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext) {
        CacheMethodContext cacheMethodContext = this.getContext(methodContext);
        final CacheManager manager = context.get(cacheMethodContext.getManager());
        String name = cacheMethodContext.getName();
        if ("".equals(name)) {
            if (methodContext.getType().isAnnotationPresent(CacheService.class)) {
                final CacheService annotation = methodContext.getType().getAnnotation(CacheService.class);
                name = annotation.value();
            } else {
                throw new IllegalStateException("Service " + methodContext.getType() + " contains cache targets but does not provide a valid ID");
            }
        }

        final Expiration expiration = cacheMethodContext.getExpiration();
        String finalName = name;

        Supplier<Cache<?>> cacheSupplier = () -> {
            Cache<Object> cache;
            if (expiration != null)
                cache = manager.getOrCreate(finalName, expiration);
            else {
                cache = manager.get(finalName).cause(() -> new IllegalStateException("Requested state '" + finalName + "' has not been initialized"));
            }
            return cache;
        };

        CacheContext cacheContext = new SimpleCacheContext(manager, cacheSupplier, name);

        return this.process(context, methodContext, cacheContext);
    }

    protected abstract <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext, CacheContext cacheContext);

    protected abstract CacheMethodContext getContext(MethodProxyContext<?> context);

    @Override
    public <T> boolean preconditions(MethodProxyContext<T> context) {
        return true;
    }

    @Override
    public Class<UseCaching> activator() {
        return UseCaching.class;
    }
}
