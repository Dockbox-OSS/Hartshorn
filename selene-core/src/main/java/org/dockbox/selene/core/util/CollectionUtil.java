/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.util;

import org.dockbox.selene.core.objects.tuple.Tuple;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unused")
public final class CollectionUtil {

    CollectionUtil() {}

    /**
     * Constructs a new unique map from a given set of {@link Entry entries}. If no entries are provided
     * {@link CollectionUtil#emptyMap()} is returned. The constructed map is not concurrent. Entries can easily be created
     * using {@link CollectionUtil#entry(Object, Object)}
     *
     * @param <K>
     *         The (super)type of all keys in the entry set
     * @param <V>
     *         The (super)type of all values in the entry set
     * @param entries
     *         The entries to use while constructing a new map
     *
     * @return The new non-concurrent map
     * @throws NullPointerException
     *         If a entry is null
     * @see CollectionUtil#entry(Object, Object)
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final <K, V> Map<K, V> ofEntries(Entry<? extends K, ? extends V>... entries) {
        if (0 == entries.length) { // implicit null check of entries array
            return this.emptyMap();
        } else {
            Map<K, V> map = this.emptyMap();
            for (Entry<? extends K, ? extends V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        }
    }

    /**
     * Returns a new empty map. This should be used globally instead of instantiating maps manually. The returned map is
     * not concurrent.
     *
     * @param <K>
     *         The (super)type of the map key-set
     * @param <V>
     *         The (super)type of the map value-set
     *
     * @return The new map
     * @see CollectionUtil#emptyConcurrentMap()
     */
    public <K, V> Map<K, V> emptyMap() {
        return new HashMap<>();
    }

    /**
     * Creates a new entry based on a given key and value combination. Both the key and value may be null.
     *
     * @param <K>
     *         The type of the key
     * @param <V>
     *         The type of the value
     * @param k
     *         The key
     * @param v
     *         The value
     *
     * @return The entry
     * @see CollectionUtil#ofEntries(Entry...)
     */
    public <K, V> Entry<K, V> entry(K k, V v) {
        return new Tuple<>(k, v);
    }


    public <T> Collection<T> singletonList(T mockWorld) {
        return Collections.singletonList(mockWorld);
    }

    public <T> List<T> emptyConcurrentList() {
        return new CopyOnWriteArrayList<>();
    }

    public <T> Set<T> emptyConcurrentSet() {
        return ConcurrentHashMap.newKeySet();
    }

    public <K, V> ConcurrentMap<K, V> emptyConcurrentMap() {
        return new ConcurrentHashMap<>();
    }


    @NotNull
    @Contract("_ -> new")
    public <T> Set<T> asSet(Collection<T> collection) {
        return new HashSet<>(collection);
    }

    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public final <T> List<T> asUnmodifiableList(T... objects) {
        return Collections.unmodifiableList(this.asList(objects));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public final <T> List<T> asList(T... objects) {
        return this.asList(Arrays.asList(objects));
    }

    public <T> List<T> asList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    public <T> List<T> asUnmodifiableList(Collection<T> collection) {
        return Collections.unmodifiableList(this.emptyList());
    }

    public <T> List<T> emptyList() {
        return new ArrayList<>();
    }

    public <T> Set<T> emptySet() {
        return new HashSet<>();
    }

    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public final <T> Set<T> asUnmodifiableSet(T... objects) {
        return Collections.unmodifiableSet(this.asSet(objects));
    }

    @NotNull
    @Contract("_ -> new")
    @SafeVarargs
    public final <T> Set<T> asSet(T... objects) {
        return new HashSet<>(this.asList(objects));
    }

    @SafeVarargs
    public final <T> Collection<T> asUnmodifiableCollection(T... collection) {
        return Collections.unmodifiableCollection(Arrays.asList(collection));
    }

    public <T> Collection<T> asUnmodifiableCollection(Collection<T> collection) {
        return Collections.unmodifiableCollection(collection);
    }

    public <K, V> Map<K, V> asUnmodifiableMap(Map<K, V> map) {
        return Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("RedundantUnmodifiable")
    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public <T> Set<T> asUnmodifiableSet(Set<T> objects) {
        return Collections.unmodifiableSet(objects);
    }

    @SuppressWarnings("RedundantUnmodifiable")
    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public <T> List<T> asUnmodifiableList(List<T> objects) {
        return Collections.unmodifiableList(objects);
    }
}
