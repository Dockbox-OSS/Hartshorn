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
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * A provider for creating collections of a given target type using a supplier for empty collections
 * with the default capacity (if applicable), and an int function to create collections with a given
 * capacity (if applicable).
 *
 * @param supplier The supplier to create empty collections
 * @param capacityConstructor The constructor to create collections with a given capacity
 * @param <T> The type of the collection
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public record SupplierCollectionProvider<T extends Collection<?>>(
    Supplier<T> supplier,
    IntFunction<T> capacityConstructor
) implements CollectionProvider<T> {

    @Override
    public T createEmpty() {
        return this.supplier.get();
    }

    @Override
    public T createWithCapacity(int capacity) {
        return this.capacityConstructor.apply(capacity);
    }
}
