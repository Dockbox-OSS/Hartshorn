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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.cache.annotations.UpdateCache;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContextImpl;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * The {@link ServiceAnnotatedMethodInterceptorPostProcessor} responsible for {@link UpdateCache}
 * decorated methods. This delegates functionality to the underlying {@link org.dockbox.hartshorn.cache.CacheManager}
 * to update specific {@link org.dockbox.hartshorn.cache.Cache caches}.
 */
public class CacheUpdateMethodPostProcessor extends CacheServicePostProcessor<UpdateCache> {

    @Override
    protected CacheMethodContext context(final MethodProxyContext<?> context) {
        final UpdateCache update = context.annotation(UpdateCache.class);
        return new CacheMethodContextImpl(update.value(), null);
    }

    @Override
    protected <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final CacheContext cacheContext) {
        return interceptorContext -> {
            try {
                final Object o = interceptorContext.args()[0];
                cacheContext.manager().get(cacheContext.cacheName())
                        .present(cache -> cache.put(cacheContext.key(), o));
                return interceptorContext.invokeDefault();
            } catch (final ApplicationException e) {
                context.handle(e);
                return methodContext.method().returnType().defaultOrNull();
            }
        };
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        return methodContext.method().parameters().size() == 1;
    }

    @Override
    public Class<UpdateCache> annotation() {
        return UpdateCache.class;
    }
}
