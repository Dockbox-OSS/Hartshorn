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

import java.util.LinkedList;
import java.util.List;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.ApplicationException;

public abstract class StrategyChainComponentProvider implements ComponentProvider {

    private final InjectionCapableApplication application;
    private final List<ComponentProviderStrategy> strategies;

    public StrategyChainComponentProvider(InjectionCapableApplication application) {
        this.application = application;
        this.strategies = new LinkedList<>();
    }

    protected void setStrategies(List<ComponentProviderStrategy> strategies) {
        this.strategies.clear();
        this.strategies.addAll(strategies);
    }

    public InjectionCapableApplication application() {
        return application;
    }

    @Override
    public <T> T get(ComponentKey<T> key, ComponentRequestContext requestContext) {
        List<ComponentProviderStrategy> strategies = List.copyOf(this.strategies);
        ComponentProviderStrategyChain<T> chain = new SimpleComponentProviderStrategyChain<>(
                this,
                this.application,
                strategies);

        final ObjectContainer<T> container;
        try {
            container = chain.get(key, requestContext);
        }
        catch(ApplicationException e) {
            throw new ComponentResolutionException("Failed to initialize component with key " + key, e);
        }

        // If the object is already processed at this point, it means that the object container was
        // reused, so we don't need to process it again. Note that this is not the same as the object
        // being a singleton, which is handled by the singleton cache.
        if (container.processed()) {
            return container.instance();
        }
        else {
            try {
                return this.process(key, requestContext, container);
            }
            catch(ApplicationException e) {
                throw new ComponentResolutionException("Failed to process component with key " + key, e);
            }
        }
    }

    protected abstract <T> T process(ComponentKey<T> key, ComponentRequestContext requestContext, ObjectContainer<T> container) throws ApplicationException;
}
