/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class CollectionUtilities {

    private static final Map<Class<?>, Supplier<Collection<?>>> COLLECTION_DEFAULTS = ofEntries(
            Tuple.of(Collection.class, ArrayList::new),
            Tuple.of(List.class, ArrayList::new),
            Tuple.of(Set.class, HashSet::new)
    );

    /**
     * Constructs a new unique map from a given set of {@link Entry entries}. If no entries are
     * provided an empty {@link Map} is returned. The constructed map is not concurrent.
     * Entries can easily be created using {@link Tuple#of(Object, Object)}
     *
     * @param <K> The (super)type of all keys in the entry set
     * @param <V> The (super)type of all values in the entry set
     * @param entries The entries to use while constructing a new map
     *
     * @return The new non-concurrent map
     * @throws NullPointerException If an entry is null
     * @see Tuple#of(Object, Object)
     */
    @SafeVarargs
    public static <K, V> Map<K, V> ofEntries(final Entry<? extends K, ? extends V>... entries) {
        if (0 == entries.length) { // implicit null check of entries array
            return new HashMap<>();
        }
        else {
            final Map<K, V> map = new HashMap<>();
            for (final Entry<? extends K, ? extends V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        }
    }

    @SafeVarargs
    public static <T> Collection<T> merge(final Collection<T>... collections) {
        final Collection<T> merged = new HashSet<>();
        for (final Collection<T> collection : collections) {
            merged.addAll(collection);
        }
        return merged;
    }

    public static <T> T[] merge(final T[] arrayOne, final T[] arrayTwo) {
        final Object[] merged = Arrays.copyOf(arrayOne, arrayOne.length + arrayTwo.length, arrayOne.getClass());
        System.arraycopy(arrayTwo, 0, merged, arrayOne.length, arrayTwo.length);
        return TypeUtils.adjustWildcards(merged, Object.class);
    }

    public static <T> Set<T> difference(final Collection<T> collectionOne, final Collection<T> collectionTwo) {
        final BiFunction<Collection<T>, Collection<T>, List<T>> filter = (c1, c2) -> c1.stream()
                .filter(element -> !c2.contains(element))
                .toList();
        final List<T> differenceInOne = filter.apply(collectionOne, collectionTwo);
        final List<T> differenceInTwo = filter.apply(collectionTwo, collectionOne);
        return Set.copyOf(CollectionUtilities.merge(differenceInOne, differenceInTwo));
    }

    public static <E, T extends Collection<E>> T transform(final Collection<E> collection, final Collection<E> initialCollection, final TypeView<? extends Collection<?>> target) {
        if (initialCollection != null) {
            initialCollection.addAll(collection);
            if (!target.isParentOf(initialCollection.getClass())) {
                throw new IllegalArgumentException("Initial collection is not of the same type as the target collection");
            }
            return TypeUtils.adjustWildcards(initialCollection, Collection.class);
        }
        else if (target.isAbstract()) {
            for (final Entry<Class<?>, Supplier<Collection<?>>> entry : COLLECTION_DEFAULTS.entrySet()) {
                if (target.is(entry.getKey())) {
                    final Collection<E> collectionInstance = TypeUtils.adjustWildcards(entry.getValue().get(), Collection.class);
                    collectionInstance.addAll(collection);
                    return TypeUtils.adjustWildcards(collectionInstance, Collection.class);
                }
            }
            throw new UnsupportedOperationException("Cannot transform to an abstract collection type (was not a Set or List), use a concrete type instead, or assign a default (empty) collection instance.");
        }
        else {
            final Result<? extends ConstructorView<? extends Collection<?>>> fromCollectionConstructor = target.constructors().withParameters(Collection.class);
            if (fromCollectionConstructor.present()) {
                final Result<? extends Collection<?>> createdCollection = fromCollectionConstructor.get().create(collection);
                if (createdCollection.present()) {
                    return TypeUtils.adjustWildcards(createdCollection.get(), Collection.class);
                }
            }
            else {
                final Result<? extends ConstructorView<? extends Collection<?>>> defaultConstructor = target.constructors().defaultConstructor();
                if (defaultConstructor.present()) {
                    final Result<? extends Collection<?>> createdCollection = defaultConstructor.get().create();
                    if (createdCollection.present()) {
                        final Collection<E> collectionInstance = TypeUtils.adjustWildcards(createdCollection.get(), Collection.class);
                        collectionInstance.addAll(collection);
                        return TypeUtils.adjustWildcards(collectionInstance, Collection.class);
                    }
                }
            }
            throw new IllegalArgumentException("Collection type " + target.name() + " does not have a constructor that accepts a Collection, or a default constructor. Can't transform collection to this type.");
        }
    }
}
