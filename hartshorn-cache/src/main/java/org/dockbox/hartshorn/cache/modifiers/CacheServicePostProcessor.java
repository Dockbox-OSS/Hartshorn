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
import org.dockbox.hartshorn.cache.annotations.CacheService;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.cache.context.CacheContextImpl;
import org.dockbox.hartshorn.cache.context.CacheMethodContext;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.ComponentUtilities;
import org.dockbox.hartshorn.core.services.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.core.proxy.MethodInterceptor;
import org.dockbox.hartshorn.core.services.ComponentProcessingContext;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Common functionality for cache related {@link ServiceAnnotatedMethodInterceptorPostProcessor modifiers}.
 *
 * @param <A> The cache-related annotation
 */
public abstract class CacheServicePostProcessor<A extends Annotation> extends ServiceAnnotatedMethodInterceptorPostProcessor<A, UseCaching> {

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        final CacheMethodContext cacheMethodContext = this.context(methodContext);
        final CacheManager manager = context.get(CacheManager.class);
        String name = cacheMethodContext.name();
        if ("".equals(name)) {
            final Exceptional<CacheService> annotation = methodContext.type().annotation(CacheService.class);
            if (annotation.present()) {
                name = annotation.get().value();
            }
            else {
                name = ComponentUtilities.id(context, methodContext.type());
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

    protected abstract <T, R> MethodInterceptor<T> process(ApplicationContext context, MethodProxyContext<T> methodContext, CacheContext cacheContext);

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        return true;
    }

    @Override
    public Class<UseCaching> activator() {
        return UseCaching.class;
    }
}
