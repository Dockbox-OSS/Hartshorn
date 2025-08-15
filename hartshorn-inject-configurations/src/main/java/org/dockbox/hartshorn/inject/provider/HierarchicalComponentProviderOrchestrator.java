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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ContextKey;
import org.dockbox.hartshorn.inject.DefaultFallbackCompatibleContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.AliasBindingFunction;
import org.dockbox.hartshorn.inject.binding.AliasCapableBinder;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingAliasNormalizer;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.DefaultBindingAliasNormalizer;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.processing.ComponentProcessorRegistry;
import org.dockbox.hartshorn.inject.processing.CompositeHierarchicalBinderPostProcessor;
import org.dockbox.hartshorn.inject.processing.ConcurrentHierarchicalBinderProcessorRegistry;
import org.dockbox.hartshorn.inject.processing.HierarchicalBinderPostProcessor;
import org.dockbox.hartshorn.inject.processing.HierarchicalBinderProcessorRegistry;
import org.dockbox.hartshorn.inject.processing.MultiMapComponentProcessorRegistry;
import org.dockbox.hartshorn.inject.processing.construction.AnnotatedMethodComponentPostConstructor;
import org.dockbox.hartshorn.inject.processing.construction.ComponentPostConstructor;
import org.dockbox.hartshorn.inject.provider.singleton.ConcurrentHashSingletonCache;
import org.dockbox.hartshorn.inject.provider.strategy.ManagedComponentProviderStrategy;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeAdapter;
import org.dockbox.hartshorn.inject.scope.ScopeModuleContext;
import org.dockbox.hartshorn.util.collections.HashSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.configure.ContextualInitializer;
import org.dockbox.hartshorn.util.configure.Customizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * A component provider orchestrator that manages component providers for different scopes, and switches between
 * them based on the scope of the requested component. This implementation supports the full range of features
 * within the Hartshorn framework, including hierarchical binding and aliasing.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class HierarchicalComponentProviderOrchestrator
        extends DefaultFallbackCompatibleContext
        implements HierarchicalComponentProvider, ComponentRegistryAwareProviderOrchestrator, AliasCapableComponentProviderOrchestrator, HierarchicalBinder, AliasCapableBinder {

    private final Map<Scope, HierarchicalAliasBinderAwareComponentProvider> scopedProviders = Collections.synchronizedMap(new WeakHashMap<>());
    private final Scope applicationScope;

    private final InjectionCapableApplication application;
    private final ComponentRegistry registry;
    private final ComponentPostConstructor postConstructor;
    private final BindingAliasNormalizer bindingAliasNormalizer;

    private final ComponentProcessorRegistry componentProcessorRegistry;
    private final HierarchicalBinderProcessorRegistry binderProcessorRegistry;

    protected HierarchicalComponentProviderOrchestrator(
        InjectionCapableApplication application,
        ComponentRegistry registry,
        ComponentPostConstructor postConstructor,
        BindingAliasNormalizer bindingAliasNormalizer
    ) {
        this.registry = registry;
        this.application = application;
        this.postConstructor = postConstructor;
        this.bindingAliasNormalizer = bindingAliasNormalizer;

        this.applicationScope = ScopeAdapter.of(this);
        this.componentProcessorRegistry = new MultiMapComponentProcessorRegistry();
        this.binderProcessorRegistry = new ConcurrentHierarchicalBinderProcessorRegistry();
    }

    @NonNull
    protected HierarchicalAliasBinderAwareComponentProvider createComponentProvider(Scope scope) {
        HierarchicalAliasBinderAwareComponentProvider provider = HierarchyAwareComponentProvider.create(
                this,
                this.postConstructor,
                this.bindingAliasNormalizer,
                this.application,
                new ConcurrentHashSingletonCache(),
                scope,
                strategies -> {
                    strategies.insert(0, new ManagedComponentProviderStrategy());
                });

        if(scope != this.application) {
            ContextKey<ScopeModuleContext> scopeModuleContextKey = ScopeModuleContext.createKey(() -> this.scope().installableScopeType());
            ScopeModuleContext scopeModuleContext = this.application.firstContext(scopeModuleContextKey).get();
            Collection<BindingHierarchy<?>> hierarchies = scopeModuleContext.hierarchies(scope.installableScopeType());
            for (BindingHierarchy<?> hierarchy : hierarchies) {
                provider.binder().bind(hierarchy);
            }
        }

        // Cache provider before processing, in case of recursive calls
        this.scopedProviders.put(scope, provider);

        HierarchicalBinderPostProcessor binderPostProcessor = new CompositeHierarchicalBinderPostProcessor(this.binderProcessorRegistry()::processors);
        binderPostProcessor.process(this.application, provider.scope(), provider.binder());
        return provider;
    }

    private HierarchicalAliasBinderAwareComponentProvider getOrDefaultProvider(Scope scope) {
        return this.tryGetProvider(scope, s -> this.getOrCreateProvider(this.applicationScope));
    }

    private HierarchicalAliasBinderAwareComponentProvider getOrCreateProvider(Scope scope) {
        return this.tryGetProvider(scope, this::createComponentProvider);
    }

    private HierarchicalAliasBinderAwareComponentProvider tryGetProvider(Scope scope, Function<Scope, HierarchicalAliasBinderAwareComponentProvider> fallbackValue) {
        if (scope == null) {
            scope = this.applicationScope;
        }
        synchronized (this.scopedProviders) {
            if (this.scopedProviders.containsKey(scope)) {
                return this.scopedProviders.get(scope);
            }
            return fallbackValue.apply(scope);
        }
    }

    public ComponentPostConstructor postConstructor() {
        return this.postConstructor;
    }

    @Override
    public ComponentRegistry componentRegistry() {
        return this.registry;
    }

    @Override
    public HierarchicalBinderProcessorRegistry binderProcessorRegistry() {
        return this.binderProcessorRegistry;
    }

    @Override
    public ComponentProcessorRegistry processorRegistry() {
        return this.componentProcessorRegistry;
    }

    @Override
    public <C> AliasBindingFunction<C> bind(ComponentKey<C> key) {
        Scope scope = key.scope().orElse(this.scope());
        return this.getOrCreateProvider(scope).binder().bind(key);
    }

    @Override
    public <C> Binder bind(BindingHierarchy<C> hierarchy) {
        Scope scope = hierarchy.key().scope().orElse(this.scope());
        return this.getOrCreateProvider(scope).binder().bind(hierarchy);
    }

    @Override
    public <T> T get(ComponentKey<T> key, ComponentRequestContext requestContext) {
        Scope scope = key.scope().orElse(this.scope());
        return this.getOrCreateProvider(scope).get(key, requestContext);
    }

    @Override
    public Scope scope() {
        return this.applicationScope;
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key) {
        Scope scope = key.scope().orElse(this.scope());
        return this.getOrDefaultProvider(scope).hierarchy(key);
    }

    @Override
    public MultiMap<Scope, BindingHierarchy<?>> hierarchies() {
        MultiMap<Scope, BindingHierarchy<?>> hierarchies = new HashSetMultiMap<>();
        for (HierarchicalComponentProvider componentProvider : this.scopedProviders.values()) {
            MultiMap<Scope, BindingHierarchy<?>> providerHierarchies = componentProvider.hierarchies();
            assert providerHierarchies.keySet().size() == 1 : "Hierarchy collection from scoped provider should only contain one scope";
            hierarchies.putAll(providerHierarchies);
        }
        return hierarchies;
    }

    @Override
    public HierarchicalComponentProvider applicationProvider() {
        return this.getOrCreateProvider(this.applicationScope);
    }

    @Override
    public boolean containsScope(Scope scopeKey) {
        return this.scopedProviders.containsKey(scopeKey);
    }

    public static ContextualInitializer<ComponentRegistry, ComponentProviderOrchestrator> create(Customizer<Configurer> customizer) {
        return context -> {
            InjectionCapableApplication application = context.firstContext(InjectionCapableApplication.class)
                    .orElseThrow(() -> new IllegalStateException("No application context found"));

            Configurer configurer = new Configurer();
            customizer.configure(configurer);

            ComponentRegistry registry = context.input();
            ComponentPostConstructor postConstructor = configurer.componentPostConstructor.initialize(context.transform(application));
            BindingAliasNormalizer bindingAliasNormalizer = configurer.bindingAliasNormalizer.initialize(context.transform(application));

            return new HierarchicalComponentProviderOrchestrator(application, registry, postConstructor, bindingAliasNormalizer);
        };
    }

    @Override
    public BindingAliasNormalizer aliasNormalizer() {
        return this.bindingAliasNormalizer;
    }

    /**
     * Configurer for the {@link HierarchicalComponentProviderOrchestrator}.
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private ContextualInitializer<InjectionCapableApplication, ComponentPostConstructor> componentPostConstructor = AnnotatedMethodComponentPostConstructor.create(Customizer.useDefaults());
        private ContextualInitializer<InjectionCapableApplication, BindingAliasNormalizer> bindingAliasNormalizer = ContextualInitializer.of(DefaultBindingAliasNormalizer::new);

        public Configurer componentPostConstructor(ComponentPostConstructor componentPostConstructor) {
            return this.componentPostConstructor(ContextualInitializer.of(componentPostConstructor));
        }

        public Configurer componentPostConstructor(ContextualInitializer<InjectionCapableApplication, ComponentPostConstructor> componentPostConstructor) {
            this.componentPostConstructor = componentPostConstructor;
            return this;
        }

        public Configurer bindingAliasNormalizer(BindingAliasNormalizer bindingAliasNormalizer) {
            return this.bindingAliasNormalizer(ContextualInitializer.of(bindingAliasNormalizer));
        }

        public Configurer bindingAliasNormalizer(ContextualInitializer<InjectionCapableApplication, BindingAliasNormalizer> bindingAliasNormalizer) {
            this.bindingAliasNormalizer = bindingAliasNormalizer;
            return this;
        }
    }
}
