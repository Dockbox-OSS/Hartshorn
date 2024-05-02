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

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.context.InstallIfAbsent;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.NativePrunableBindingHierarchy;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;

@InstallIfAbsent
public class ScopeModuleContext extends DefaultApplicationAwareContext {

    private final MultiMap<ScopeKey, BindingHierarchy<?>> scopeModules = new ConcurrentSetMultiMap<>();

    @Inject
    public ScopeModuleContext(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public <T> BindingHierarchy<T> hierarchy(ScopeKey scope, ComponentKey<T> key) {
        BindingHierarchy<?> bindingHierarchy = this.scopeModules.get(scope).stream()
                .filter(hierarchy -> hierarchy.key().equals(key))
                .findFirst()
                .orElseGet(() -> {
                    BindingHierarchy<T> hierarchy = new NativePrunableBindingHierarchy<>(key, this.applicationContext());
                    this.scopeModules.put(scope, hierarchy);
                    return hierarchy;
                });

        return TypeUtils.adjustWildcards(bindingHierarchy, BindingHierarchy.class);
    }

    public Collection<BindingHierarchy<?>> hierarchies(ScopeKey type) {
        if (type == ApplicationContext.APPLICATION_SCOPE) {
            return Collections.emptyList();
        }
        return this.scopeModules.get(type);
    }
}
