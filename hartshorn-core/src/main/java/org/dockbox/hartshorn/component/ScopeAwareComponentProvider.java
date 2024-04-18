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

package org.dockbox.hartshorn.component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.DefaultBindingConfigurerContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.collections.HashSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;

public class ScopeAwareComponentProvider extends DefaultProvisionContext implements HierarchicalComponentProvider, ScopedProviderOwner {

    private final transient ApplicationContext applicationContext;
    private final transient ComponentRegistry registry;

    private final transient MultiMap<Integer, ComponentPostProcessor> postProcessors = new ConcurrentSetTreeMultiMap<>();
    private final transient ComponentPostConstructor postConstructor;
    private final Map<Scope, HierarchyAwareComponentProvider> scopedProviders = Collections.synchronizedMap(new WeakHashMap<>());
    private final Set<Class<? extends ComponentPostProcessor>> uninitializedPostProcessors = ConcurrentHashMap.newKeySet();

    protected ScopeAwareComponentProvider(SingleElementContext<? extends ComponentRegistry> initializerContext, Configurer configurer) {
        this.registry = initializerContext.input();
        this.applicationContext = this.registry.applicationContext();

        SingleElementContext<ApplicationContext> applicationInitializerContext = initializerContext.transform(this.applicationContext);
        this.postConstructor = configurer.componentPostConstructor.initialize(applicationInitializerContext);

        // Eagerly initialize the application provider
        this.getOrCreateProvider(this.applicationContext);
    }

    private HierarchyAwareComponentProvider getOrCreateProvider(Scope scope) {
        if (scope == null) {
            scope = this.applicationContext;
        }
        synchronized (this.scopedProviders) {
            return this.scopedProviders.computeIfAbsent(scope, this::createComponentProvider);
        }
    }

    @NonNull
    private HierarchyAwareComponentProvider createComponentProvider(Scope scope) {
        HierarchyAwareComponentProvider provider = new HierarchyAwareComponentProvider(this, scope);
        if (scope != this.applicationContext) {
            ScopeModuleContext scopeModuleContext = this.applicationContext.firstContext(ScopeModuleContext.class).get();
            Collection<BindingHierarchy<?>> hierarchies = scopeModuleContext.hierarchies(scope.installableScopeType());
            for (BindingHierarchy<?> hierarchy : hierarchies) {
                provider.bind(hierarchy);
            }
        }
        return provider;
    }

    private HierarchyAwareComponentProvider getOrDefaultProvider(Scope scope) {
        if (scope == null) {
            scope = this.applicationContext;
        }
        synchronized (this.scopedProviders) {
            return this.scopedProviders.get(scope);
        }
    }

    @Override
    public ComponentRegistry componentRegistry() {
        return this.registry;
    }

    @Override
    public ComponentPostConstructor postConstructor() {
        return this.postConstructor;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public <C> BindingFunction<C> bind(ComponentKey<C> key) {
        return this.getOrCreateProvider(key.scope()).bind(key);
    }

    @Override
    public <C> Binder bind(BindingHierarchy<C> hierarchy) {
        return this.getOrCreateProvider(hierarchy.key().scope()).bind(hierarchy);
    }

    @Override
    public void postProcessor(ComponentPostProcessor postProcessor) {
        this.postProcessors.put(postProcessor.priority(), postProcessor);
        this.uninitializedPostProcessors.remove(postProcessor.getClass());

        ComponentKey<ComponentPostProcessor> key = TypeUtils.adjustWildcards(ComponentKey.of(postProcessor.getClass()), ComponentKey.class);
        // Install to application context
        this.bind(key).singleton(postProcessor);
    }

    @Override
    public void postProcessor(Class<? extends ComponentPostProcessor> postProcessor) {
        boolean alreadyInitialized = this.postProcessors.allValues().stream()
                .anyMatch(processor -> processor.getClass().equals(postProcessor));
        if (!alreadyInitialized) {
            this.uninitializedPostProcessors.add(postProcessor);
        }
    }

    @Override
    public <T> T get(ComponentKey<T> key, ComponentRequestContext requestContext) {
        return this.getOrCreateProvider(key.scope()).get(key, requestContext);
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key) {
        return this.getOrDefaultProvider(key.scope()).hierarchy(key);
    }

    @Override
    public MultiMap<Scope, BindingHierarchy<?>> hierarchies() {
        MultiMap<Scope, BindingHierarchy<?>> hierarchies = new HashSetMultiMap<>();
        for (HierarchyAwareComponentProvider componentProvider : this.scopedProviders.values()) {
            MultiMap<Scope, BindingHierarchy<?>> providerHierarchies = componentProvider.hierarchies();
            assert providerHierarchies.keySet().size() == 1 : "Hierarchy collection from scoped provider should only contain one scope";
            hierarchies.putAll(providerHierarchies);
        }
        return hierarchies;
    }

    @Override
    public MultiMap<Integer, ComponentPostProcessor> postProcessors() {
        return this.postProcessors;
    }

    @Override
    public Set<Class<? extends ComponentPostProcessor>> uninitializedPostProcessors() {
        return Set.copyOf(this.uninitializedPostProcessors);
    }

    @Override
    public HierarchicalComponentProvider applicationProvider() {
        return this.getOrCreateProvider(this.applicationContext);
    }

    public static ContextualInitializer<ComponentRegistry, ComponentProvider> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);

            ScopeAwareComponentProvider componentProvider = new ScopeAwareComponentProvider(context, configurer);
            DefaultBindingConfigurerContext.compose(context, binder -> {
                binder.bind(ComponentRegistry.class).singleton(componentProvider.registry);
            });
            return componentProvider;
        };
    }

    public static class Configurer {

        private ContextualInitializer<ApplicationContext, ComponentPostConstructor> componentPostConstructor = ComponentPostConstructorImpl.create(Customizer.useDefaults());

        public Configurer componentPostConstructor(ComponentPostConstructor componentPostConstructor) {
            return this.componentPostConstructor(ContextualInitializer.of(componentPostConstructor));
        }

        public Configurer componentPostConstructor(ContextualInitializer<ApplicationContext, ComponentPostConstructor> componentPostConstructor) {
            this.componentPostConstructor = componentPostConstructor;
            return this;
        }
    }
}
