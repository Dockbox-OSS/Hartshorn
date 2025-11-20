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
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;

import java.util.Set;

/**
 * A binding hierarchy that supports aliases. Aliases are additional keys that can be used to
 * reference the same binding. This is useful for example when a binding is defined in multiple
 * modules, and you want to reference the same binding using different keys.
 *
 * @param <C> The type of the component that this hierarchy is for.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface AliasableBindingHierarchy<C> extends BindingHierarchy<C> {

    /**
     * Add an alias to the current hierarchy. Note that this will affect all providers in the
     * hierarchy, at all configured priorities.
     *
     * @param componentKey The key to register as an alias.
     */
    void alias(ComponentKey<? super C> componentKey);

    /**
     * Returns all aliases that are registered for this hierarchy.
     *
     * @return All aliases.
     */
    Set<ComponentKey<? super C>> aliases();

    @Override
    AliasableBindingHierarchy<C> add(InstantiationStrategy<C> strategy);

    @Override
    AliasableBindingHierarchy<C> add(int priority, InstantiationStrategy<C> strategy);

    @Override
    AliasableBindingHierarchy<C> addNext(InstantiationStrategy<C> strategy);

    @Override
    AliasableBindingHierarchy<C> merge(BindingHierarchy<C> hierarchy);
}
