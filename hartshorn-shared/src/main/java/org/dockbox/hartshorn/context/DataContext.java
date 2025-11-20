/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.context;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A context that allows for storing and retrieving custom data entries associated with specific
 * types.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class DataContext extends DefaultContext {

    private final Map<Class<?>, Object> data = new ConcurrentHashMap<>();

    /**
     * Returns the number of custom data entries stored in this context.
     *
     * @return the size of the data map
     */
    public int size() {
        return this.data.size();
    }

    /**
     * Checks if this context contains no custom data entries.
     *
     * @return {@code true} if the data map is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    /**
     * Checks if this context contains a custom data entry for the given type.
     *
     * @param type the class type to check for
     *
     * @return {@code true} if the type exists in the data map, {@code false} otherwise
     */
    public boolean containsKey(Class<?> type) {
        return this.data.containsKey(type);
    }

    /**
     * Retrieves the value associated with the given type from the context's data map.
     *
     * @param type the class type for which to retrieve the value
     * @param <R> the type of the value
     *
     * @return the value associated with the type, or {@code null} if not found
     */
    public <R> R get(Class<R> type) {
        return type.cast(this.data.get(type));
    }

    /**
     * Stores a value in the context's data map under the specified type.
     *
     * @param type the class type under which to store the value
     * @param value the value to store
     * @param <R> the type of the value
     *
     * @return the previous value associated with the type, or {@code null} if there was no mapping
     */
    public <R> R put(Class<R> type, R value) {
        return type.cast(this.data.put(type, value));
    }

    /**
     * Removes the value associated with the specified key from the context's data map.
     *
     * @param key the key of the value to remove
     * @param <R> the type of the value
     *
     * @return the removed value, or {@code null} if there was no mapping for the key
     */
    public <R> R remove(Class<R> key) {
        return key.cast(this.data.remove(key));
    }

    /**
     * Clears all custom data entries from this context.
     */
    public void clear() {
        this.data.clear();
    }

    /**
     * Returns a set view of the keys contained in this context's data map.
     *
     * @return a set of keys
     */
    public Set<Class<?>> keySet() {
        return this.data.keySet();
    }

    /**
     * Returns the value associated with the specified type, or a default value if the type is not
     * present.
     *
     * @param type the class type for which to retrieve the value
     * @param defaultValue the default value to return if the type is not present
     * @param <R> the type of the value
     *
     * @return the value associated with the type, or the default value if the type is not present
     */
    public <R> R getOrDefault(Class<R> type, R defaultValue) {
        return type.cast(this.data.getOrDefault(type, defaultValue));
    }

    /**
     * Puts a value into the context's data map if the type is not already present.
     *
     * @param type the class type under which to store the value
     * @param value the value to store
     * @param <R> the type of the value
     *
     * @return the previous value associated with the type, or {@code null} if there was no mapping
     */
    public <R> R putIfAbsent(Class<R> type, R value) {
        return type.cast(this.data.putIfAbsent(type, value));
    }

    /**
     * Removes a specific value associated with the given type from the context's data map.
     *
     * @param type the class type of the value to remove
     * @param value the value to remove
     * @param <R> the type of the value
     *
     * @return {@code true} if the value was removed, {@code false} otherwise
     */
    public <R> boolean remove(Class<R> type, R value) {
        return this.data.remove(type, value);
    }

    /**
     * Replaces a specific value associated with the given type in the context's data map.
     *
     * @param type the class type of the value to replace
     * @param oldValue the current value to be replaced
     * @param newValue the new value to set
     * @param <R> the type of the value
     *
     * @return {@code true} if the value was replaced, {@code false} otherwise
     */
    public <R> boolean replace(Class<R> type, R oldValue, R newValue) {
        return this.data.replace(type, oldValue, newValue);
    }

    /**
     * Computes a value for the given type if it is not already present in the context's data map.
     *
     * @param type the class type for which to compute the value
     * @param mappingFunction the function to compute the value
     * @param <R> the type of the value
     *
     * @return the computed value associated with the type
     */
    public <R> R computeIfAbsent(
        Class<R> type,
        Function<? super Class<R>, R> mappingFunction
    ) {
        return type.cast(
            this.data.computeIfAbsent(type, componentKey -> mappingFunction.apply(type))
        );
    }
}
