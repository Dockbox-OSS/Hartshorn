/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.context.SimpleSingleElementContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.BindingAliasNormalizer;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchicalAliasCapableBinder;
import org.dockbox.hartshorn.inject.binding.NestedHierarchyLookup;
import org.dockbox.hartshorn.inject.binding.ScopeAwareHierarchicalBinder;
import org.dockbox.hartshorn.inject.processing.ComponentProviderPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProviderPostProcessorAdapter;
import org.dockbox.hartshorn.inject.processing.ComponentStoreCallback;
import org.dockbox.hartshorn.inject.processing.CompositeComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.PostConstructingComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.construction.ComponentPostConstructor;
import org.dockbox.hartshorn.inject.provider.singleton.SingletonCache;
import org.dockbox.hartshorn.inject.provider.strategy.ComponentProcessorComponentProviderStrategy;
import org.dockbox.hartshorn.inject.provider.strategy.ComponentProviderStrategy;
import org.dockbox.hartshorn.inject.provider.strategy.InstantiationStrategyComponentProviderStrategy;
import org.dockbox.hartshorn.inject.provider.strategy.SingletonCacheComponentProviderStrategy;
import org.dockbox.hartshorn.inject.provider.strategy.StrategyChainComponentProvider;
import org.dockbox.hartshorn.inject.provider.strategy.UnboundPrototypeComponentProviderStrategy;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.configure.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.configure.StreamableConfigurer;

import java.util.List;

/**
 * A {@link ComponentProvider} which is aware of the {@link Scope} in which it is installed, and
 * tracks bindings based on available {@link BindingHierarchy binding hierarchies}. This allows for
 * the creation of a hierarchy of bindings, which can be used to resolve components at specific
 * priorities.
 *
 * <p>As this provider is aware of the {@link Scope} in which it is installed, it is constrained to
 * be part of a
 * {@link ComponentProviderOrchestrator}. This orchestrator is responsible for providing the
 * {@link Scope} in which this provider is installed.
 *
 * @author Guus Lieben
 * @see ComponentProviderOrchestrator
 * @see HierarchicalComponentProvider
 * @since 0.4.10
 */
public class HierarchyAwareComponentProvider extends StrategyChainComponentProvider
    implements HierarchicalAliasBinderAwareComponentProvider, SingletonCacheComponentProvider,
    NestedHierarchyLookup {

    private final ComponentProviderPostProcessor processor;
    private final HierarchicalAliasCapableBinder binder;
    private final Scope scope;
    private final SingletonCache singletonCache;

    public HierarchyAwareComponentProvider(
        ComponentRegistryAwareProviderOrchestrator orchestrator,
        ComponentPostConstructor postConstructor,
        BindingAliasNormalizer bindingAliasNormalizer,
        InjectionCapableApplication application,
        SingletonCache singletonCache,
        Scope scope
    ) {
        this(
            application,
            singletonCache,
            new ScopeAwareHierarchicalBinder(application,
                bindingAliasNormalizer,
                singletonCache,
                scope),
            createProviderPostProcessor(singletonCache, orchestrator, application, postConstructor),
            scope
        );
    }

    public HierarchyAwareComponentProvider(
        InjectionCapableApplication application,
        SingletonCache singletonCache,
        HierarchicalAliasCapableBinder binder,
        ComponentProviderPostProcessor postProcessor,
        Scope scope
    ) {
        super(application);

        this.singletonCache = singletonCache;
        this.binder = binder;
        this.processor = postProcessor;
        this.scope = scope;
    }

    /**
     * Creates a standard {@link ComponentProviderPostProcessor} for this provider, which combines
     * the post-processors registered in the orchestrator, and uses a local cache for store
     * callbacks.
     *
     * @param singletonCache the singleton cache to use for store callbacks
     * @param orchestrator the orchestrator to retrieve post-processors from
     * @param application the application this provider is part of
     * @param postConstructor the post constructor to use
     *
     * @return a new component provider post-processor
     */
    protected static ComponentProviderPostProcessor createProviderPostProcessor(
        SingletonCache singletonCache,
        ComponentRegistryAwareProviderOrchestrator orchestrator,
        InjectionCapableApplication application,
        ComponentPostConstructor postConstructor
    ) {
        CompositeComponentPostProcessor postProcessor =
            new CompositeComponentPostProcessor(() -> orchestrator.processorRegistry()
                .postProcessors());
        ComponentStoreCallback storeCallback = new LocalCacheComponentStoreCallback(singletonCache);
        ComponentProviderPostProcessor standardProcessor =
            new ComponentProviderPostProcessorAdapter(
                orchestrator,
                postProcessor,
                application,
                new LocalCacheComponentStoreCallback(singletonCache)
            );
        return new PostConstructingComponentPostProcessor(
            postConstructor,
            standardProcessor,
            storeCallback,
            orchestrator.scope()
        );
    }

    @Override
    protected <T> T process(
        ComponentKey<T> key,
        ComponentRequestContext requestContext,
        ObjectContainer<T> container
    )
        throws ApplicationException {
        try {
            return this.processor.processInstance(key, container, requestContext);
        }
        catch (ApplicationException e) {
            throw new ComponentResolutionException("Failed to process component with key " + key,
                e);
        }
    }

    @Override
    public HierarchicalAliasCapableBinder binder() {
        return this.binder;
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
        if (this.binder instanceof NestedHierarchyLookup containedHierarchicalBinder) {
            return containedHierarchicalBinder.hierarchy(key, useGlobalIfAbsent);
        }
        return this.binder.hierarchy(key);
    }

    /**
     * Creates a new {@link HierarchyAwareComponentProvider} with the given parameters. The created
     * provider will be configured with a default set of
     * {@link ComponentProviderStrategy strategies}, which can be customized using the provided
     * {@link Customizer}.
     *
     * @param orchestrator the orchestrator this provider is part of
     * @param postConstructor the post constructor to use for component post-construction
     * @param bindingAliasNormalizer the binding alias normalizer to use
     * @param application the application this provider is part of
     * @param singletonCache the singleton cache to use
     * @param scope the scope this provider is installed in
     * @param strategyCustomizer a customizer to customize the default strategies
     *
     * @return a new hierarchy-aware component provider
     */
    public static HierarchyAwareComponentProvider create(
        ComponentRegistryAwareProviderOrchestrator orchestrator,
        ComponentPostConstructor postConstructor,
        BindingAliasNormalizer bindingAliasNormalizer,
        InjectionCapableApplication application,
        SingletonCache singletonCache,
        Scope scope,
        Customizer<StreamableConfigurer<InjectionCapableApplication, ComponentProviderStrategy>> strategyCustomizer
    ) {
        LazyStreamableConfigurer<InjectionCapableApplication, ComponentProviderStrategy>
            strategyConfigurer = LazyStreamableConfigurer.of(
            configurer -> {
                configurer.add(new SingletonCacheComponentProviderStrategy());
                configurer.add(new ComponentProcessorComponentProviderStrategy());
                configurer.add(new InstantiationStrategyComponentProviderStrategy(scope));
                configurer.add(new UnboundPrototypeComponentProviderStrategy());
            });

        List<ComponentProviderStrategy> strategies =
            strategyConfigurer.customizer(strategyCustomizer)
                .initialize(SimpleSingleElementContext.create(application));

        HierarchyAwareComponentProvider provider = new HierarchyAwareComponentProvider(
            orchestrator,
            postConstructor,
            bindingAliasNormalizer,
            application,
            singletonCache,
            scope
        );
        provider.strategies(strategies);
        return provider;
    }
}
