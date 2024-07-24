/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.inject.provider.strategy;

import java.util.List;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.graph.support.ComponentInitializationException;
import org.dockbox.hartshorn.inject.provider.ComponentObjectContainer;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.util.ApplicationException;

public class SimpleComponentProviderStrategyChain<T> implements ComponentProviderStrategyChain<T> {

    private final InjectionCapableApplication application;
    private final List<ComponentProviderStrategy> strategies;
    private final ComponentProvider componentProvider;
    private final int index;

    public SimpleComponentProviderStrategyChain(
            ComponentProvider componentProvider,
            InjectionCapableApplication application,
            List<ComponentProviderStrategy> strategies) {
        this(componentProvider, application, strategies, 0);
    }

    public SimpleComponentProviderStrategyChain(
            ComponentProvider componentProvider,
            InjectionCapableApplication application,
            List<ComponentProviderStrategy> strategies,
            int index
    ) {
        this.componentProvider = componentProvider;
        this.application = application;
        this.strategies = strategies;
        this.index = index;
    }

    @Override
    public ComponentProvider componentProvider() {
        return componentProvider;
    }

    @Override
    public InjectionCapableApplication application() {
        return null;
    }

    @Override
    public ObjectContainer<T> get(ComponentKey<T> componentKey, ComponentRequestContext requestContext)
            throws ComponentInitializationException, ApplicationException {
        if(this.index < this.strategies.size()) {
            ComponentProviderStrategy strategy = this.strategies.get(index);
            ComponentProviderStrategyChain<T> chain = new SimpleComponentProviderStrategyChain<>(
                    this.componentProvider,
                    this.application,
                    this.strategies,
                    this.index + 1
            );
            return strategy.get(componentKey, requestContext, chain);
        }
        return ComponentObjectContainer.empty();
    }
}
