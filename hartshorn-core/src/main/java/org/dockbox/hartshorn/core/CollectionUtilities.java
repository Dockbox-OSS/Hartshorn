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

package org.dockbox.hartshorn.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

public final class CollectionUtilities {

    /**
     * Constructs a new unique map from a given set of {@link Entry entries}. If no entries are
     * provided an empty {@link Map} is returned. The constructed map is not concurrent.
     * Entries can easily be created using {@link org.dockbox.hartshorn.core.domain.tuple.Tuple#of(Object, Object)}
     *
     * @param <K> The (super)type of all keys in the entry set
     * @param <V> The (super)type of all values in the entry set
     * @param entries The entries to use while constructing a new map
     *
     * @return The new non-concurrent map
     * @throws NullPointerException If an entry is null
     * @see org.dockbox.hartshorn.core.domain.tuple.Tuple#of(Object, Object)
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

    public static <T> Set<T> difference(final Collection<T> collectionOne, final Collection<T> collectionTwo) {
        final BiFunction<Collection<T>, Collection<T>, List<T>> filter = (c1, c2) -> c1.stream()
                .filter(element -> !c2.contains(element))
                .toList();
        final List<T> differenceInOne = filter.apply(collectionOne, collectionTwo);
        final List<T> differenceInTwo = filter.apply(collectionTwo, collectionOne);
        return Set.copyOf(CollectionUtilities.merge(differenceInOne, differenceInTwo));
    }

}
