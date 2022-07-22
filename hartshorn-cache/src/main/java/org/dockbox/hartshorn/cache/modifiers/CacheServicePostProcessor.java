/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.cache.modifiers;

import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.Expiration;
import org.dockbox.hartshorn.cache.KeyGenerator;
import org.dockbox.hartshorn.cache.annotations.CacheDecorator;
import org.dockbox.hartshorn.cache.annotations.CacheService;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.cache.context.CacheContextImpl;
import org.dockbox.hartshorn.cache.context.CacheMethodContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.component.ComponentUtilities;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.util.StringUtilities;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Common functionality for cache related {@link ServiceAnnotatedMethodInterceptorPostProcessor modifiers}.
 *
 * @param <A> The cache-related annotation
 * @author Guus Lieben
 * @since 21.2
 */
public abstract class CacheServicePostProcessor<A extends Annotation> extends ServiceAnnotatedMethodInterceptorPostProcessor<A> {

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> proxyContext, final ComponentProcessingContext processingContext) {
        final CacheMethodContext cacheMethodContext = this.context(proxyContext);
        final CacheManager manager = context.get(CacheManager.class);

        String name = cacheMethodContext.name();
        if ("".equals(name)) {
            final Result<CacheService> annotation = proxyContext.type().annotation(CacheService.class);
            if (annotation.present()) {
                name = annotation.get().value();
            }
            else {
                name = ComponentUtilities.id(context, proxyContext.type());
            }
        }

        final Expiration expiration = cacheMethodContext.expiration();
        final String finalName = name;

        context.log().debug("Determined cache name '" + finalName + "' for " + proxyContext.method().qualifiedName());

        final Supplier<Cache<?, ?>> cacheSupplier = () -> {
            final Cache<Object, Object> cache;
            if (expiration != null)
                cache = manager.getOrCreate(finalName, expiration);
            else {
                cache = manager.get(finalName)
                        .orThrow(() -> new UnavailableCacheException(finalName));
            }
            return cache;
        };

        final CacheDecorator cacheDecorator = proxyContext.annotation(CacheDecorator.class);
        final KeyGenerator keyGenerator = context.get(cacheDecorator.keyGenerator());

        String key = cacheDecorator.key();
        if (StringUtilities.empty(key)) {
            key = keyGenerator.generateKey(proxyContext.method());
        }
        final CacheContext cacheContext = new CacheContextImpl(manager, cacheSupplier, finalName, key);

        return this.process(context, proxyContext, cacheContext);
    }

    protected abstract CacheMethodContext context(MethodProxyContext<?> context);

    protected abstract <T, R> MethodInterceptor<T> process(ApplicationContext context, MethodProxyContext<T> methodContext, CacheContext cacheContext);

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        return true;
    }
}
