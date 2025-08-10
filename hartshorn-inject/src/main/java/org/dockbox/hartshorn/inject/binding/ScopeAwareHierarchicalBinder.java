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

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.singleton.SingletonCache;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeModuleContext;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link HierarchicalBinder} that is aware of the scope it is bound to. This binder will only allow
 * bindings to be added to the scope it is bound to, and will not allow bindings to be added to a different
 * scope.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ScopeAwareHierarchicalBinder extends SimpleHierarchicalBinder {

    private final Scope scope;

    public ScopeAwareHierarchicalBinder(
        InjectionCapableApplication application,
        BindingAliasNormalizer bindingAliasNormalizer,
        SingletonCache singletonCache,
        Scope scope
    ) {
        super(application, bindingAliasNormalizer, singletonCache);
        this.scope = scope;
    }

    protected Scope scope() {
        return this.scope;
    }

    @Override
    public <C> AliasBindingFunction<C> bind(Class<C> type) {
        // Strict, so new hierarchies are created if needed, rather than using loose lookup
        ComponentKey<C> componentKey = ComponentKey.builder(type)
                .strict(true)
                .scope(this.scope) // No explicit scope provided, so expected to use the current scope instead
                .build();
        return this.bind(componentKey);
    }

    @Override
    protected <C> AliasBindingFunction<C> bind(Scope scope, ComponentKey<C> key) {
        if (scope != this.scope() && scope != this.applicationScope()) {
            throw new IllegalArgumentException(
                    "Cannot bind to a different scope. Expected %s, got %s for key %s".formatted(this.scope(), scope, key));
        }
        return super.bind(this.scope(), key);
    }

    @Override
    protected Option<ScopeModuleContext> resolveScopeModuleContext() {
        Option<ScopeModuleContext> scopeModuleContext = super.resolveScopeModuleContext();
        if (scopeModuleContext.absent() && this.scope() != this.applicationScope()) {
            throw new IllegalModificationException("Cannot add binding to non-application hierarchy without a module context");
        }
        return scopeModuleContext;
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key, boolean useGlobalIfAbsent) {
        // If the scope is default, it means that the binding is not explicitly scoped, so it can be
        // installed in any scope. If our active scope is the active application context, it means
        // the requested scope is not installed, so we can fall back to the application scope.
        if (!key.scope().contains(this.scope()) && this.scope() != this.applicationScope()) {
            throw new IllegalArgumentException("Cannot create a binding hierarchy for a component key with a different scope");
        }
        return super.hierarchy(key, useGlobalIfAbsent);
    }

    @Override
    protected void populateHierarchies(MultiMap<Scope, BindingHierarchy<?>> hierarchies) {
        hierarchies.putAll(this.scope(), this.hierarchyCache().hierarchies());
    }
}
