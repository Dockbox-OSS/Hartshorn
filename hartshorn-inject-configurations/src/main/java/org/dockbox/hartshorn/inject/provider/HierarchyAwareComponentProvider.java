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

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.processing.ComponentProviderPostProcessor;
import org.dockbox.hartshorn.inject.processing.SimpleComponentProviderPostProcessor;
import org.dockbox.hartshorn.inject.processing.CompositeComponentPostProcessor;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.inject.ContextKey;
import org.dockbox.hartshorn.inject.DefaultFallbackCompatibleContext;
import org.dockbox.hartshorn.inject.graph.support.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.processing.construction.ComponentPostConstructor;
import org.dockbox.hartshorn.inject.scope.ScopeModuleContext;
import org.dockbox.hartshorn.inject.provider.singleton.ConcurrentHashSingletonCache;
import org.dockbox.hartshorn.inject.binding.SubscribableBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchyBindingFunction;
import org.dockbox.hartshorn.inject.provider.singleton.SingletonCache;
import org.dockbox.hartshorn.inject.collection.CollectionBindingHierarchy;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.HashSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link ComponentProvider} which is aware of the {@link Scope} in which it is installed, and tracks bindings
 * based on available {@link BindingHierarchy binding hierarchies}. This allows for the creation of a hierarchy of
 * bindings, which can be used to resolve components at specific priorities.
 *
 * <p>As this provider is aware of the {@link Scope} in which it is installed, it is constrained to be part of a
 * {@link ScopeAwareComponentProvider}. This owner is responsible for providing the {@link Scope} in which this provider is
 * installed.
 *
 * @see ScopeAwareComponentProvider
 * @see HierarchicalComponentProvider
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public class HierarchyAwareComponentProvider extends DefaultFallbackCompatibleContext
        implements HierarchicalComponentProvider, SingletonCacheComponentProvider, HierarchicalBinder {

    private final ScopeAwareComponentProvider owner;
    private final Scope scope;

    private final SingletonCache singletonCache = new ConcurrentHashSingletonCache();
    private final ComponentProviderPostProcessor processor;
    private final InjectionCapableApplication application;
    private HierarchyCache hierarchyCache;

    public HierarchyAwareComponentProvider(ScopeAwareComponentProvider owner, ComponentPostConstructor postConstructor,
            InjectionCapableApplication application, Scope scope) {
        this.owner = owner;
        this.scope = scope;
        this.application = application;
        CompositeComponentPostProcessor postProcessor = new CompositeComponentPostProcessor(() -> owner.processorRegistry().postProcessors());
        this.processor = new SimpleComponentProviderPostProcessor(
            owner,
            postConstructor,
            postProcessor,
            application,
            new LocalCacheComponentStoreCallback(this.singletonCache)
        );
    }

    @Override
    public SingletonCache singletonCache() {
        return this.singletonCache;
    }

    public HierarchyCache hierarchyCache() {
        if (this.hierarchyCache == null ) {
            this.hierarchyCache = new HierarchyCache(this.application.environment().configuration(), this.owner.applicationProvider(), this);
        }
        return this.hierarchyCache;
    }

    private <T> Option<ObjectContainer<T>> create(ComponentKey<T> key, ComponentRequestContext requestContext) {
        try {
            Option<ObjectContainer<T>> objectContainer = this.provide(key, requestContext);
            if (objectContainer.present()) {
                return objectContainer;
            }

            return this.createContextualInstanceContainer(key, requestContext);
        } catch (ApplicationException e) {
            throw new ComponentInitializationException("Failed to create component for key " + key, e);
        }
    }

    @Override
    public <C> BindingFunction<C> bind(ComponentKey<C> key) {
        Scope componentScope = key.scope().orNull();
        if (componentScope == null) {
            componentScope = this.owner.applicationProvider().scope();
        }

        if (componentScope != this.scope && componentScope != this.owner.applicationProvider().scope()) {
            throw new IllegalArgumentException("Cannot bind to a different scope. Expected " + this.scope + ", got " + componentScope
                + " for key " + key);
        }
        BindingHierarchy<C> hierarchy = this.hierarchy(key);

        ContextIdentity<ScopeModuleContext> scopeModuleContextKey = ContextKey.builder(ScopeModuleContext.class)
                .fallback(() -> new ScopeModuleContext(this.owner.applicationProvider().scope().installableScopeType()))
                .build();
        Option<ScopeModuleContext> scopeModuleContext = this.application.firstContext(scopeModuleContextKey);

        if (scopeModuleContext.absent() && this.scope != this.owner.applicationProvider().scope()) {
            throw new IllegalModificationException("Cannot add binding to non-application hierarchy without a module context");
        }

        return new HierarchyBindingFunction<>(hierarchy, this, this.singletonCache, this.scope, scopeModuleContext.orNull());
    }

    @Override
    public <C> Binder bind(BindingHierarchy<C> hierarchy) {
        this.hierarchyCache().put(hierarchy);
        return this;
    }

    protected <T> Option<ObjectContainer<T>> provide(ComponentKey<T> key, ComponentRequestContext requestContext) throws ApplicationException {
        Option<BindingHierarchy<T>> hierarchy = Option.of(this.hierarchy(key, true));
        if (hierarchy.present()) {
            Provider<T> provider = key.strategy().selectProvider(hierarchy.get());
            if (provider != null) {
                return provider.provide(requestContext);
            }
        }
        return Option.empty();
    }

    protected <T> Option<ObjectContainer<T>> createContextualInstanceContainer(ComponentKey<T> key, ComponentRequestContext requestContext) throws ApplicationException {
        return ContextDrivenProvider.forPrototype(key).provide(requestContext);
    }

    @Override
    public <T> T get(ComponentKey<T> componentKey, ComponentRequestContext requestContext) {
        if (this.singletonCache.contains(componentKey)) {
            return this.singletonCache.get(componentKey)
                .orElseThrow(() -> new ComponentResolutionException("No instance found for key " + componentKey + ", but the key was present in the singleton cache"));
        }

        ObjectContainer<T> objectContainer = this.create(componentKey, requestContext)
                .orElseGet(ComponentObjectContainer::empty);

        // If the object is already processed at this point, it means that the object container was
        // reused, so we don't need to process it again. Note that this is not the same as the object
        // being a singleton, which is handled by the singleton cache.
        if (objectContainer.processed()) {
            return objectContainer.instance();
        }

        try {
            return this.processor.processInstance(componentKey, objectContainer, requestContext);
        }
        catch(ApplicationException e) {
            throw new ComponentResolutionException("Failed to process component with key " + componentKey, e);
        }
    }

    @Override
    public Scope scope() {
        return this.scope;
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key) {
        return this.hierarchy(key, false);
    }

    @Override
    public MultiMap<Scope, BindingHierarchy<?>> hierarchies() {
        MultiMap<Scope, BindingHierarchy<?>> map = new HashSetMultiMap<>();
        map.putAll(this.scope, this.hierarchyCache().hierarchies());
        return map;
    }

    private <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key, boolean permitFallbackResolution) {
        // If the scope is default, it means that the binding is not explicitly scoped, so it can be
        // installed in any scope. If our active scope is the active application context, it means
        // the requested scope is not installed, so we can fall back to the application scope.
        if (!key.scope().equals(this.scope) && this.scope != this.owner.applicationProvider().scope()) {
            throw new IllegalArgumentException("Cannot create a binding hierarchy for a component key with a different scope");
        }

        HierarchyCache cache = this.hierarchyCache();
        BindingHierarchy<?> hierarchy = cache.getOrComputeHierarchy(key, permitFallbackResolution);
        BindingHierarchy<T> adjustedHierarchy = TypeUtils.unchecked(hierarchy, BindingHierarchy.class);
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (adjustedHierarchy instanceof SubscribableBindingHierarchy || adjustedHierarchy instanceof CollectionBindingHierarchy<?>) {
            return adjustedHierarchy;
        }
        else {
            return new SubscribableBindingHierarchy<>(adjustedHierarchy, updated -> cache.put(key.view(), updated));
        }
    }
}
