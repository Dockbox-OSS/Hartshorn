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

package org.dockbox.hartshorn.inject.provider;

import java.util.List;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.ContainedHierarchyLookup;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.processing.ComponentProviderPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentStoreCallback;
import org.dockbox.hartshorn.inject.processing.PostConstructingComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.SimpleComponentProviderPostProcessor;
import org.dockbox.hartshorn.inject.processing.CompositeComponentPostProcessor;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.processing.construction.ComponentPostConstructor;
import org.dockbox.hartshorn.inject.provider.singleton.SingletonCache;
import org.dockbox.hartshorn.inject.provider.strategy.ComponentProcessorComponentProviderStrategy;
import org.dockbox.hartshorn.inject.provider.strategy.InstantiationStrategyComponentProviderStrategy;
import org.dockbox.hartshorn.inject.provider.strategy.SingletonCacheComponentProviderStrategy;
import org.dockbox.hartshorn.inject.provider.strategy.StrategyChainComponentProvider;
import org.dockbox.hartshorn.inject.provider.strategy.UnboundPrototypeComponentProviderStrategy;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.collections.MultiMap;

/**
 * A {@link ComponentProvider} which is aware of the {@link Scope} in which it is installed, and tracks bindings
 * based on available {@link BindingHierarchy binding hierarchies}. This allows for the creation of a hierarchy of
 * bindings, which can be used to resolve components at specific priorities.
 *
 * <p>As this provider is aware of the {@link Scope} in which it is installed, it is constrained to be part of a
 * {@link ComponentProviderOrchestrator}. This orchestrator is responsible for providing the {@link Scope} in which
 * this provider is installed.
 *
 * @see ComponentProviderOrchestrator
 * @see HierarchicalComponentProvider
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public class HierarchyAwareComponentProvider extends StrategyChainComponentProvider
        implements HierarchicalBinderAwareComponentProvider, SingletonCacheComponentProvider, ContainedHierarchyLookup {

    private final ComponentProviderOrchestrator orchestrator;
    private final ComponentProviderPostProcessor processor;
    private final HierarchicalBinder binder;
    private final Scope scope;
    private final SingletonCache singletonCache;

    public HierarchyAwareComponentProvider(
            ComponentProviderOrchestrator orchestrator,
            ComponentPostConstructor postConstructor,
            InjectionCapableApplication application,
            HierarchicalBinder binder,
            Scope scope,
            SingletonCache singletonCache
    ) {
        super(application);
        this.orchestrator = orchestrator;
        this.singletonCache = singletonCache;
        this.processor = createProviderPostProcessor(postConstructor);
        this.binder = binder;
        this.scope = scope;

        // TODO: Configurer?
        setStrategies(List.of(
                new SingletonCacheComponentProviderStrategy(),
                new ComponentProcessorComponentProviderStrategy(),
                new InstantiationStrategyComponentProviderStrategy(),
                new UnboundPrototypeComponentProviderStrategy()
        ));
    }

    private ComponentProviderPostProcessor createProviderPostProcessor(ComponentPostConstructor postConstructor) {
        CompositeComponentPostProcessor postProcessor = new CompositeComponentPostProcessor(() -> this.orchestrator.processorRegistry().postProcessors());
        ComponentStoreCallback storeCallback = new LocalCacheComponentStoreCallback(this.singletonCache);
        ComponentProviderPostProcessor standardProcessor = new SimpleComponentProviderPostProcessor(
                this.orchestrator,
                postProcessor,
                this.application(),
                new LocalCacheComponentStoreCallback(this.singletonCache)
        );
        return new PostConstructingComponentPostProcessor(
                postConstructor,
                standardProcessor,
                storeCallback,
                this.orchestrator.scope()
        );
    }

    @Override
    protected <T> T process(ComponentKey<T> key, ComponentRequestContext requestContext, ObjectContainer<T> container)
            throws ApplicationException {
        try {
            return this.processor.processInstance(key, container, requestContext);
        }
        catch(ApplicationException e) {
            throw new ComponentResolutionException("Failed to process component with key " + key, e);
        }
    }

    @Override
    public HierarchicalBinder binder() {
        return binder;
    }

    @Override
    public SingletonCache singletonCache() {
        return this.singletonCache;
    }

    @Override
    public Scope scope() {
        return this.scope;
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key) {
        return this.binder.hierarchy(key);
    }

    @Override
    public MultiMap<Scope, BindingHierarchy<?>> hierarchies() {
        return this.binder.hierarchies();
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key, boolean useGlobalIfAbsent) {
        if (this.binder instanceof ContainedHierarchyLookup containedHierarchicalBinder) {
            return containedHierarchicalBinder.hierarchy(key, useGlobalIfAbsent);
        }
        return this.binder.hierarchy(key);
    }
}
