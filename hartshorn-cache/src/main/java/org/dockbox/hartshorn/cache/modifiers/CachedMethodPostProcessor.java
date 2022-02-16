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
import org.dockbox.hartshorn.cache.Expiration;
import org.dockbox.hartshorn.cache.annotations.Cached;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContextImpl;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.MethodInterceptor;
import org.dockbox.hartshorn.core.services.ComponentProcessingContext;
import org.dockbox.hartshorn.core.services.ServiceAnnotatedMethodInterceptorPostProcessor;

/**
 * The {@link ServiceAnnotatedMethodInterceptorPostProcessor} responsible for {@link Cached}
 * decorated methods. This delegates functionality to the underlying {@link org.dockbox.hartshorn.cache.CacheManager}
 * to store or obtain {@link Cache} entries.
 */
@AutomaticActivation
public class CachedMethodPostProcessor extends CacheServicePostProcessor<Cached> {

    @Override
    protected <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final CacheContext cacheContext) {
        return (interceptorContext) -> {
            final Cache<Object> cache = cacheContext.cache();

            final Exceptional<Object> content = cache.get();

            return content.orElse(() -> {
                context.log().debug("Cache " + cacheContext.name() + " has not been populated yet, or content has expired.");
                try {
                    final Object out = interceptorContext.invokeDefault();
                    cache.populate(out);
                    return out;
                }
                catch (final Throwable e) {
                    context.handle(e);
                    return null;
                }
            }).orNull(); // In case of void returns
        };
    }

    @Override
    protected CacheMethodContext context(final MethodProxyContext<?> context) {
        final Cached cached = context.annotation(Cached.class);
        return new CacheMethodContextImpl(cached.value(), Expiration.of(cached.expires()));
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        return !methodContext.method().returnType().isVoid();
    }

    @Override
    public Class<Cached> annotation() {
        return Cached.class;
    }
}
