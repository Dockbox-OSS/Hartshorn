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

package org.dockbox.hartshorn.inject.scope;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ContextKey;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.NativePrunableBindingHierarchy;
import org.dockbox.hartshorn.util.collections.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * A context that manages default bindings for scope modules. The hierarchies of bindings are stored
 * based on their scope keys, allowing them to be re-used for different instances of the same scope.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ScopeModuleContext extends DefaultContext {

    private final MultiMap<ScopeKey, BindingHierarchy<?>> scopeModules = new ConcurrentSetMultiMap<>();

    private final ScopeKey applicationScope;

    public ScopeModuleContext(ScopeKey applicationScope) {
        this.applicationScope = applicationScope;
    }

    /**
     * Creates a context key for the {@link ScopeModuleContext}. If there is no existing context for the
     * {@link ScopeModuleContext}, a new one will be created using the provided fallback scope key.
     *
     * @param fallbackScope the supplier for the fallback scope key
     * @return a context key for the {@link ScopeModuleContext}
     */
    public static ContextKey<ScopeModuleContext> createKey(Supplier<ScopeKey> fallbackScope) {
        return ContextKey.builder(ScopeModuleContext.class)
            .fallback(() -> new ScopeModuleContext(fallbackScope.get()))
            .build();
    }

    /**
     * Checks if the provided scope key is the application scope.
     *
     * @param scopeKey the scope key to check
     * @return true if the scope key is the application scope, false otherwise
     */
    public boolean isApplicationScope(ScopeKey scopeKey) {
        return this.applicationScope.equals(scopeKey);
    }

    /**
     * Retrieves the binding hierarchy for the specified scope and component key. If no hierarchy exists,
     * a new empty one is created and added to the context.
     *
     * @param scope the scope key for which to retrieve the hierarchy
     * @param key the component key for which to retrieve the hierarchy
     * @param <T> the type of the component
     * @return the binding hierarchy for the specified scope and component key
     */
    public <T> BindingHierarchy<T> hierarchy(ScopeKey scope, ComponentKey<T> key) {
        BindingHierarchy<?> bindingHierarchy = this.scopeModules.get(scope).stream()
                .filter(hierarchy -> hierarchy.isCompatible(key))
                .findFirst()
                .orElseGet(() -> {
                    BindingHierarchy<T> hierarchy = new NativePrunableBindingHierarchy<>(key);
                    this.scopeModules.put(scope, hierarchy);
                    return hierarchy;
                });

        return TypeUtils.unchecked(bindingHierarchy, BindingHierarchy.class);
    }

    /**
     * Retrieves all binding hierarchies associated with the specified scope key.
     *
     * @param type the scope key for which to retrieve the hierarchies
     * @return a collection of binding hierarchies for the specified scope key, or an empty collection if none exist
     */
    public Collection<BindingHierarchy<?>> hierarchies(ScopeKey type) {
        if (type == this.applicationScope) {
            return Collections.emptyList();
        }
        return this.scopeModules.get(type);
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("applicationScope", this.applicationScope)
                .field("scopeModules", this.scopeModules)
                .describe();
    }
}
