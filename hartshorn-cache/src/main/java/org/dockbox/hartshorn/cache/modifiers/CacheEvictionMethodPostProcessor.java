/*
 * Copyright 2019-2023 the original author or authors.
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
import org.dockbox.hartshorn.cache.Expiration;
import org.dockbox.hartshorn.cache.annotations.EvictCache;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContextImpl;
import org.dockbox.hartshorn.component.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.component.processing.proxy.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;

/**
 * The {@link ServiceAnnotatedMethodInterceptorPostProcessor} responsible for {@link EvictCache}
 * decorated methods. This delegates functionality to the underlying {@link org.dockbox.hartshorn.cache.CacheManager}
 * to evict specific {@link org.dockbox.hartshorn.cache.Cache caches}.
 *
 * @author Guus Lieben
 * @since 21.2
 */
public class CacheEvictionMethodPostProcessor extends CacheServicePostProcessor<EvictCache> {

    @Override
    protected CacheMethodContext context(final MethodProxyContext<?> context) {
        final EvictCache evict = context.annotation(EvictCache.class);
        return new CacheMethodContextImpl(evict.value(), Expiration.never());
    }

    @Override
    protected <T, R> MethodInterceptor<T, R> process(final ApplicationContext context, final CacheContext cacheContext) {
        return new CacheEvictionMethodInterceptor<>(cacheContext, context);
    }

    @Override
    public Class<EvictCache> annotation() {
        return EvictCache.class;
    }
}
