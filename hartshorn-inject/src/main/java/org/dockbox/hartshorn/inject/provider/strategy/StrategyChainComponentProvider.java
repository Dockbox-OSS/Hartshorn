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

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.LinkedList;
import java.util.List;
import java.util.SequencedCollection;

/**
 * A {@link ComponentProvider} that uses a chain of {@link ComponentProviderStrategy strategies} to resolve components.
 * Each component request is assigned a unique {@link ComponentProviderStrategyChain} that is used to resolve the
 * component. The provider itself does not make any guarantees about the nullability of the resolved component.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class StrategyChainComponentProvider implements ComponentProvider {

    private final InjectionCapableApplication application;
    private final SequencedCollection<ComponentProviderStrategy> strategies;

    public StrategyChainComponentProvider(InjectionCapableApplication application) {
        this.application = application;
        this.strategies = new LinkedList<>();
    }

    /**
     * Sets the strategies that this provider will use to resolve components. This will replace any existing strategies.
     *
     * @param strategies the strategies to set
     */
    protected void strategies(SequencedCollection<ComponentProviderStrategy> strategies) {
        this.strategies.clear();
        this.strategies.addAll(strategies);
    }

    /**
     * The application that this provider is associated with.
     *
     * @return the injection-capable application
     */
    public InjectionCapableApplication application() {
        return this.application;
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

    /**
     * Processes the given {@link ObjectContainer} before returning the instance. This method is called
     * after the component has been resolved by the strategy chain, but before the instance is returned.
     * This allows for additional processing of the instance, such as initialization or proxying.
     *
     * @param key the component key used to resolve the component
     * @param requestContext the request context
     * @param container the object container holding the resolved instance
     * @param <T> the type of the component
     *
     * @return the processed instance
     *
     * @throws ApplicationException if processing fails
     */
    protected abstract <T> T process(ComponentKey<T> key, ComponentRequestContext requestContext, ObjectContainer<T> container) throws ApplicationException;
}
