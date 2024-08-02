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

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.collections.MultiMap;

/**
 * A provider that supports hierarchical bindings. This means that bindings are not just one-to-one, but can be
 * one-to-many, with various bindings on different priority levels.
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public interface HierarchicalComponentProvider extends ComponentProvider {

    /**
     * Returns the binding hierarchy for the given key.
     *
     * @param key the key to return the hierarchy for
     * @param <T> the type of the component
     *
     * @return the binding hierarchy
     */
    <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key);

    /**
     * Returns all binding hierarchies in the current provider.
     *
     * @return all binding hierarchies
     */
    MultiMap<Scope, BindingHierarchy<?>> hierarchies();
}
