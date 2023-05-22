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
import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.context.CacheContext;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.option.Option;

public class CachedMethodInterceptor<T, R> implements MethodInterceptor<T, R> {
    private final CacheContext cacheContext;
    private final String elementKey;
    private final ConversionService conversionService;
    private final ApplicationContext context;

    public CachedMethodInterceptor(final CacheContext cacheContext, final String elementKey, final ConversionService conversionService, final ApplicationContext context) {
        this.cacheContext = cacheContext;
        this.elementKey = elementKey;
        this.conversionService = conversionService;
        this.context = context;
    }

    @Override
    public R intercept(final MethodInterceptorContext<T, R> interceptorContext) {
        final Cache<String, Object> cache = this.cacheContext.cache();
        final Option<R> content = cache.get(this.elementKey)
                .map(result -> this.conversionService.convert(result, interceptorContext.method().returnType().type()));

        return content.orElseGet(() -> {
            this.context.log().debug("Cache " + this.cacheContext.cacheName() + " has not been populated yet, or content has expired.");
            try {
                final R out = interceptorContext.invokeDefault();
                cache.putIfAbsent(this.elementKey, out);
                return out;
            }
            catch (final Throwable e) {
                this.context.handle(e);
                return null;
            }
        });
    }
}
