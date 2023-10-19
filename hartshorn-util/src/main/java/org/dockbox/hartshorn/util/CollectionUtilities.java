/*
 * Copyright 2019-2023 the original author or authors.
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
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CollectionUtilities {

    public static final Map<Class<?>, Supplier<Collection<?>>> COLLECTION_DEFAULTS = Map.ofEntries(
            Map.entry(Collection.class, ArrayList::new),
            Map.entry(List.class, ArrayList::new),
            Map.entry(Set.class, HashSet::new)
    );

    private CollectionUtilities() {
    }

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
     * @deprecated Use {@link Map#ofEntries(Entry...)} instead
     */
    @SafeVarargs
    @Deprecated
    public static <K, V> Map<K, V> ofEntries(Entry<? extends K, ? extends V>... entries) {
        if (0 == entries.length) { // implicit null check of entries array
            return new HashMap<>();
        }
        else {
            Map<K, V> map = new HashMap<>();
            for (Entry<? extends K, ? extends V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        }
    }

    @SafeVarargs
    public static <T> Set<T> merge(Collection<T>... collections) {
        Set<T> merged = new HashSet<>();
        for (Collection<T> collection : collections) {
            merged.addAll(collection);
        }
        return merged;
    }

    @SafeVarargs
    public static <T> List<T> mergeList(Collection<T>... collections) {
        List<T> merged = new ArrayList<>();
        for (Collection<T> collection : collections) {
            merged.addAll(collection);
        }
        return merged;
    }

    public static <T> T[] merge(T[] arrayOne, T[] arrayTwo) {
        T[] merged = Arrays.copyOf(arrayOne, arrayOne.length + arrayTwo.length);
        System.arraycopy(arrayTwo, 0, merged, arrayOne.length, arrayTwo.length);
        return merged;
    }

    public static <T> Set<T> difference(Collection<T> collectionOne, Collection<T> collectionTwo) {
        BiFunction<Collection<T>, Collection<T>, List<T>> filter = (c1, c2) -> c1.stream()
                .filter(element -> !c2.contains(element))
                .toList();

        List<T> differenceInOne = filter.apply(collectionOne, collectionTwo);
        List<T> differenceInTwo = filter.apply(collectionTwo, collectionOne);

        List<T> mergedDifference = new ArrayList<>(differenceInOne.size() + differenceInTwo.size());
        mergedDifference.addAll(differenceInOne);
        mergedDifference.addAll(differenceInTwo);

        return Set.copyOf(mergedDifference);
    }

    @SafeVarargs
    public static <T> void forEach(Consumer<T> consumer, Collection<T>... collections) {
        for (Collection<T> collection : collections) {
            collection.forEach(consumer);
        }
    }

    public static <T> List<T> distinct(List<T> collection) {
        return collection.stream().distinct().toList();
    }
}
