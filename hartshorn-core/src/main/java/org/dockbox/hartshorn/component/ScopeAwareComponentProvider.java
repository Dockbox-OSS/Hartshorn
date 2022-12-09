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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.InitializingContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.ComponentInstanceFactory;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class ScopeAwareComponentProvider extends DefaultContext implements StandardComponentProvider, ContextCarrier, ScopedProviderOwner {

    private final transient ApplicationContext applicationContext;
    private final transient ComponentLocator locator;

    private final transient MultiMap<Integer, ComponentPostProcessor> postProcessors = new ConcurrentSetTreeMultiMap<>();
    private final transient ComponentInstanceFactory factory;
    private final transient ComponentPostConstructor postConstructor;
    private final Map<Scope, HierarchyAwareComponentProvider> scopedProviders = Collections.synchronizedMap(new WeakHashMap<>());
    private final HierarchyAwareComponentProvider applicationComponentProvider;

    public ScopeAwareComponentProvider(final InitializingContext context) {
        this.applicationContext = context.applicationContext();
        this.locator = context.componentLocator();
        this.postConstructor = context.componentPostConstructor();
        this.factory = this::raw;
        this.applicationComponentProvider = this.getOrCreateProvider(this.applicationContext);
    }

    private HierarchyAwareComponentProvider getOrCreateProvider(final Scope scope) {
        if (scope == Scope.DEFAULT_SCOPE || scope == null) return this.applicationComponentProvider;
        synchronized (this.scopedProviders) {
            return this.scopedProviders.computeIfAbsent(scope, this::createComponentProvider);
        }
    }

    @NotNull
    private HierarchyAwareComponentProvider createComponentProvider(final Scope scope) {
        final HierarchyAwareComponentProvider provider = new HierarchyAwareComponentProvider(this, scope);
        if (scope != this.applicationContext && scope != Scope.DEFAULT_SCOPE) {
            final ScopeModuleContext scopeModuleContext = this.applicationContext.first(ScopeModuleContext.class).get();
            final Collection<BindingHierarchy<?>> hierarchies = scopeModuleContext.hierarchies(scope.installableScopeType());
            for (final BindingHierarchy<?> hierarchy : hierarchies) {
                provider.bind(hierarchy);
            }
        }
        return provider;
    }

    private HierarchyAwareComponentProvider getOrDefaultProvider(final Scope scope) {
        if (scope == Scope.DEFAULT_SCOPE) return this.applicationComponentProvider;
        synchronized (this.scopedProviders) {
            return this.scopedProviders.getOrDefault(scope, this.applicationComponentProvider);
        }
    }

    @Override
    public ComponentLocator componentLocator() {
        return this.locator;
    }

    @Override
    public ComponentInstanceFactory instanceFactory() {
        return this.factory;
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
    public <C> BindingFunction<C> bind(final ComponentKey<C> key) {
        return this.getOrCreateProvider(key.scope()).bind(key);
    }

    @Override
    public <C> Binder bind(final BindingHierarchy<C> hierarchy) {
        return this.getOrCreateProvider(hierarchy.key().scope()).bind(hierarchy);
    }

    @Override
    public void postProcessor(final ComponentPostProcessor postProcessor) {
        this.postProcessors.put(postProcessor.order(), postProcessor);
        final ComponentKey<ComponentPostProcessor> key = TypeUtils.adjustWildcards(ComponentKey.of(postProcessor.getClass()), ComponentKey.class);
        // Install to application context
        this.bind(key).singleton(postProcessor);
    }

    public <T> Option<ObjectContainer<T>> raw(final ComponentKey<T> key) {
        return new ContextDrivenProvider<>(key).provide(this.applicationContext());
    }

    @Override
    public <T> T get(final ComponentKey<T> componentKey) {
        return this.getOrCreateProvider(componentKey.scope()).get(componentKey);
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(final ComponentKey<T> key) {
        return this.getOrDefaultProvider(key.scope()).hierarchy(key);
    }

    @Override
    public MultiMap<Integer, ComponentPostProcessor> postProcessors() {
        return this.postProcessors;
    }

    @Override
    public HierarchicalComponentProvider applicationProvider() {
        return this.applicationComponentProvider;
    }
}
