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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Simple implementation of {@link CollectionFactory} that uses {@link CollectionProvider}s if possible, and
 * otherwise will attempt to create collections using the default constructor or a constructor with a single
 * int parameter (for capacity).
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@SuppressWarnings("rawtypes")
public class SimpleCollectionFactory implements CollectionFactory {

    private static final int USE_COLLECTION_DEFAULT_CAPACITY = -1;

    private final Introspector introspector;
    private final Map<Class<?>, CollectionProvider> defaults = new HashMap<>();

    public SimpleCollectionFactory(Introspector introspector) {
        this.introspector = introspector;
    }

    public <T extends Collection<?>> SimpleCollectionFactory withDefault(Class<T> type, CollectionProvider<T> provider) {
        this.defaults.put(type, provider);
        return this;
    }

    public <T extends Collection<?>> SimpleCollectionFactory withDefault(Class<T> type, Supplier<T> supplier) {
        this.defaults.put(type, new SupplierCollectionProvider<>(supplier, capacity -> supplier.get()));
        return this;
    }

    public <T extends Collection<?>> SimpleCollectionFactory withDefault(Class<T> type, Supplier<T> supplier, IntFunction<T> capacityConstructor) {
        this.defaults.put(type, new SupplierCollectionProvider<>(supplier, capacityConstructor));
        return this;
    }

    public SimpleCollectionFactory withDefaults() {
        return this
                .withDefault(Collection.class, ArrayList::new, ArrayList::new)
                .withDefault(List.class, ArrayList::new, ArrayList::new)
                .withDefault(Set.class, HashSet::new, HashSet::new)
                .withDefault(SortedSet.class, TreeSet::new)
                .withDefault(Queue.class, LinkedList::new)
                .withDefault(Deque.class, LinkedList::new)
                ;
    }

    @Override
    public <O extends Collection<E>, E> O createCollection(Class<O> targetType, Class<E> elementType) {
        return this.createCollection(targetType, elementType, USE_COLLECTION_DEFAULT_CAPACITY);
    }

    @Override
    public <O extends Collection<E>, E> O createCollection(Class<O> targetType, Class<E> elementType, int length) {
        O collection = this.maybeCreateCollectionFromDefaults(targetType, elementType, length);
        if (collection != null) {
            return collection;
        }

        if (length != USE_COLLECTION_DEFAULT_CAPACITY) {
            collection = this.createCollectionWithCapacity(targetType, length);
        }
        if (collection == null) {
            collection = this.createCollectionWithDefaultConstructor(targetType);
        }
        if(collection != null) {
            return collection;
        }
        throw new IllegalArgumentException("Unsupported Collection implementation: " + targetType.getName());
    }

    private <O extends Collection> @Nullable O createCollectionWithCapacity(Class<O> targetType, int length) {
        TypeConstructorsIntrospector<O> constructors = this.introspector.introspect(targetType).constructors();
        Option<ConstructorView<O>> defaultCapacityConstructor = constructors.withParameters(int.class);
        if (defaultCapacityConstructor.present()) {
            try {
                return defaultCapacityConstructor.get().create(length).orNull();
            }
            catch (Throwable e) {
                throw new IllegalArgumentException("Failed to create collection of type " + targetType.getName(), e);
            }
        }
        return null;
    }

    private <O extends Collection> @Nullable O createCollectionWithDefaultConstructor(Class<O> targetType) {
        TypeConstructorsIntrospector<O> constructors = this.introspector.introspect(targetType).constructors();
        Option<ConstructorView<O>> defaultConstructor = constructors.defaultConstructor();
        if (defaultConstructor.present()) {
            try {
                return defaultConstructor.get().create().orNull();
            }
            catch (Throwable e) {
                throw new IllegalArgumentException("Failed to create collection of type " + targetType.getName(), e);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <O extends Collection> @Nullable O maybeCreateCollectionFromDefaults(Class<O> targetType, Class<?> elementType, int length) {
        CollectionProvider<?> supplier = this.defaults.get(targetType);
        if (supplier != null) {
            return length != USE_COLLECTION_DEFAULT_CAPACITY
                    ? (O) supplier.createWithCapacity(length)
                    : (O) supplier.createEmpty();
        }
        else if (targetType == EnumSet.class || EnumSet.class.isAssignableFrom(targetType)) {
            if (elementType.isEnum()) {
                return (O) EnumSet.noneOf((Class<? extends Enum>) elementType);
            }
            else {
                throw new IllegalArgumentException("EnumSet must be created with an enum type");
            }
        }
        return null;
    }
}
