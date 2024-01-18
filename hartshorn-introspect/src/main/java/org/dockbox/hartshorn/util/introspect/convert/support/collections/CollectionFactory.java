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
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;

/**
 * A factory for creating collections of a given type, with a given element type. This is typically
 * used to support {@link DefaultValueProvider}s for collections.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface CollectionFactory {

    /**
     * Creates a collection of the given type, with the given element type. The collection is
     * guaranteed to be empty, and will have the default capacity for the collection type if
     * applicable.
     *
     * @param targetType The type of the collection to create
     * @param elementType The type of the elements in the collection
     * @return The created collection
     * @param <O> The type of the collection
     * @param <E> The type of the elements in the collection
     */
    <O extends Collection<E>, E> O createCollection(Class<O> targetType, Class<E> elementType);

    /**
     * Creates a collection of the given type, with the given element type. The collection is
     * guaranteed to be empty, and will have (at least) the given capacity if applicable.
     *
     * @param targetType The type of the collection to create
     * @param elementType The type of the elements in the collection
     * @param length The capacity of the collection
     * @return The created collection
     * @param <O> The type of the collection
     * @param <E> The type of the elements in the collection
     */
    <O extends Collection<E>, E> O createCollection(Class<O> targetType, Class<E> elementType, int length);

}
