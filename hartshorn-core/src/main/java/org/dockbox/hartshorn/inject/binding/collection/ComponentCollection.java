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

package org.dockbox.hartshorn.inject.binding.collection;

import java.util.Collection;
import java.util.Set;
import org.dockbox.hartshorn.inject.ObjectContainer;

/**
 * A collection of components. This is used to collect multiple bindings for the same type. The collection is
 * immutable, and does not allow duplicate entries.
 *
 * <p>The collection is automatically populated from a {@link CollectionBindingHierarchy}, and can be collected
 * using {@link org.dockbox.hartshorn.component.ComponentKey#collect(Class)}.
 *
 * @param <T> the type of the components
 *
 * @see CollectionBindingHierarchy
 * @see org.dockbox.hartshorn.component.ComponentKey#collect(Class)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ComponentCollection<T> extends Collection<T> {

    /**
     * Returns all containers in the collection. Each container represents a single binding for the type.
     *
     * @return all containers in the collection
     */
    Set<ObjectContainer<T>> containers();
}
