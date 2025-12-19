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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.collection.CollectionBindingHierarchy;
import org.dockbox.hartshorn.inject.provider.singleton.SingletonCache;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeModuleContext;
import org.dockbox.hartshorn.util.collections.HashSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.UnmodifiableMultiMap;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.TypeUtils;

/**
 * Standard implementation of {@link HierarchicalBinder}, with additional support for aliasing and
 * complex hierarchy lookups. By default, this binder solely supports the global application scope.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleHierarchicalBinder
    implements HierarchicalAliasCapableBinder, NestedHierarchyLookup {

    private final InjectionCapableApplication application;
    private final BindingAliasNormalizer bindingAliasNormalizer;
    private final SingletonCache singletonCache;

    private HierarchyCache hierarchyCache;

    public SimpleHierarchicalBinder(
        InjectionCapableApplication application,
        BindingAliasNormalizer bindingAliasNormalizer,
        SingletonCache singletonCache
    ) {
        this.application = application;
        this.bindingAliasNormalizer = bindingAliasNormalizer;
        this.singletonCache = singletonCache;
    }

    /**
     * Returns the application scope.
     *
     * @return The application scope.
     */
    protected Scope applicationScope() {
        return this.application.defaultProvider().scope();
    }

    /**
     * Returns the hierarchy cache, initializing it if necessary.
     *
     * @return The hierarchy cache.
     */
    public HierarchyCache hierarchyCache() {
        if (this.hierarchyCache == null) {
            this.hierarchyCache = new HierarchyCache(
                this.application.environment().configuration(),
                this.application.defaultBinder(),
                this
            );
        }
        return this.hierarchyCache;
    }

    @Override
    public <C> AliasBindingFunction<C> bind(ComponentKey<C> key) {
        Scope componentScope = key.scope().orNull();
        if (componentScope == null) {
            componentScope = this.applicationScope();
        }
        return this.bind(componentScope, key);
    }

    /**
     * Creates a new {@link BindingFunction} for the given component key within the specified
     * scope.
     *
     * @param scope The scope in which to bind the component.
     * @param key The component key to bind.
     * @param <C> The type of the component to bind.
     *
     * @return The binding function for the specified component key.
     */
    protected <C> AliasBindingFunction<C> bind(Scope scope, ComponentKey<C> key) {
        BindingHierarchy<C> hierarchy = this.hierarchy(key);
        AliasableBindingHierarchy<C> aliasableHierarchy =
            hierarchy instanceof AliasableBindingHierarchy<C> aliasable
                ? aliasable
                : new AliasableBindingHierarchyAdapter<>(hierarchy);

        Option<ScopeModuleContext> scopeModuleContext = resolveScopeModuleContext();
        return new HierarchyBindingFunction<>(
            aliasableHierarchy, this, this.singletonCache,
            scope, scopeModuleContext.orNull(),
            this.bindingAliasNormalizer
        );
    }

    /**
     * Resolves the {@link ScopeModuleContext} from the application, or initializes a new one if
     * none is found.
     *
     * @return The resolved or newly created scope module context.
     */
    protected Option<ScopeModuleContext> resolveScopeModuleContext() {
        ContextIdentity<ScopeModuleContext> scopeModuleContextKey = ScopeModuleContext.createKey(
            () -> this.applicationScope().installableScopeType()
        );
        return this.application.firstContext(scopeModuleContextKey);
    }

    @Override
    public <C> Binder bind(BindingHierarchy<C> hierarchy) {
        this.hierarchyCache().put(hierarchy);
        return this;
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key) {
        return this.hierarchy(key, false);
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key, boolean useGlobalIfAbsent) {
        HierarchyCache cache = this.hierarchyCache();
        BindingHierarchy<?> hierarchy = cache.getOrComputeHierarchy(key, useGlobalIfAbsent);
        BindingHierarchy<T> adjustedHierarchy =
            TypeUtils.unchecked(hierarchy, BindingHierarchy.class);
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (adjustedHierarchy instanceof SubscribableBindingHierarchy
            || adjustedHierarchy instanceof CollectionBindingHierarchy<?>) {
            return adjustedHierarchy;
        }
        else {
            return new SubscribableBindingHierarchy<>(adjustedHierarchy,
                updated -> cache.put(key.view(), updated));
        }
    }

    @Override
    public MultiMap<Scope, BindingHierarchy<?>> hierarchies() {
        MultiMap<Scope, BindingHierarchy<?>> map = new HashSetMultiMap<>();
        this.populateHierarchies(map);
        return new UnmodifiableMultiMap<>(map);
    }

    /**
     * Populates the given hierarchies map with all hierarchies from the application scope.
     *
     * @param hierarchies The hierarchies map to populate.
     */
    protected void populateHierarchies(MultiMap<Scope, BindingHierarchy<?>> hierarchies) {
        hierarchies.putAll(this.applicationScope(), this.hierarchyCache().hierarchies());
    }

    @Override
    public Scope scope() {
        return this.applicationScope();
    }
}
