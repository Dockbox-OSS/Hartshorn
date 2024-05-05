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

package org.dockbox.hartshorn.util.introspect.convert.support.collections;

import java.util.Collection;

/**
 * A provider for creating collections of a given target type. This is typically used to support
 * {@link org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider}s for collections.
 *
 * @param <T> The type of the collection
 *
 * @see CollectionFactory
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface CollectionProvider<T extends Collection<?>> {

    /**
     * Creates an empty collection of the target type. The collection is guaranteed to be empty, and
     * will have the default capacity for the collection type if applicable.
     *
     * @return The created collection
     */
    T createEmpty();

    /**
     * Creates a collection of the target type, with (at least) the given capacity if applicable.
     *
     * @param capacity The capacity of the collection
     * @return The created collection
     */
    T createWithCapacity(int capacity);
}
