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

package org.dockbox.hartshorn.util.resources;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.contextual.StaticComponentContext;
import org.dockbox.hartshorn.component.contextual.StaticComponentObserver;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.context.ContextKey;

import java.util.List;

@Service
public class ResourceLookupStrategyStaticComponentObserver implements StaticComponentObserver {

    @Override
    public void onStaticComponentsCollected(final ApplicationContext applicationContext, final StaticComponentContext staticComponentContext) {
        final List<ResourceLookupStrategy> strategies = staticComponentContext.provider().all(ResourceLookupStrategy.class);
        final ContextKey<ResourceLookupStrategyContext> contextKey = ContextKey.builder(ResourceLookupStrategyContext.class)
                .fallback(ResourceLookupStrategyContext::new)
                .build();
        final ResourceLookupStrategyContext strategyContext = applicationContext.first(contextKey).get();
        strategies.forEach(strategyContext::addLookupStrategy);
    }
}
