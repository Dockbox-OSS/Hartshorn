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

import org.dockbox.hartshorn.cache.annotations.EvictCache;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContext;
import org.dockbox.hartshorn.cache.context.CacheMethodContextImpl;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.MethodInterceptor;
import org.dockbox.hartshorn.core.services.ServiceAnnotatedMethodInterceptorPostProcessor;

/**
 * The {@link ServiceAnnotatedMethodInterceptorPostProcessor} responsible for {@link EvictCache}
 * decorated methods. This delegates functionality to the underlying {@link org.dockbox.hartshorn.cache.CacheManager}
 * to evict specific {@link org.dockbox.hartshorn.cache.Cache caches}.
 */
@AutomaticActivation
public class CacheEvictionMethodPostProcessor extends CacheServicePostProcessor<EvictCache> {

    @Override
    protected CacheMethodContext context(final MethodProxyContext<?> context) {
        final EvictCache evict = context.annotation(EvictCache.class);
        return new CacheMethodContextImpl(evict.value(), null);
    }

    @Override
    protected <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final CacheContext cacheContext) {
        return interceptorContext -> {
            try {
                cacheContext.manager().evict(cacheContext.name());
                return interceptorContext.invokeDefault();
            } catch (final ApplicationException e) {
                context.handle(e);
            }
            return null; // Should be void anyway
        };
    }

    @Override
    public Class<EvictCache> annotation() {
        return EvictCache.class;
    }
}
