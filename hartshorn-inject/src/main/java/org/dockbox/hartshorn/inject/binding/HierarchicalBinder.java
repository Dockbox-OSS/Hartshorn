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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.ComponentKey;

/**
 * A binder that supports hierarchical bindings. This means that bindings are not just one-to-one, but can be
 * one-to-many, with various bindings on different priority levels.
 *
 * @see BindingHierarchy
 * @see Binder
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface HierarchicalBinder extends Binder {

    /**
     * Returns the binding hierarchy for the given key.
     *
     * @param key the key to return the hierarchy for
     * @return the binding hierarchy
     * @param <T> the type of the component
     */
    <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key);
}
