/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.util.collections;

import org.dockbox.hartshorn.util.types.TypeUtils;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * A simple implementation of a Least Frequently Used (LFU) cache. This cache evicts the least
 * frequently used item when the capacity is exceeded. It uses an ArrayDeque to store the items in
 * the cache.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class LFUCache<K, V> {

    @SuppressWarnings("UnnecessaryLambda")
    private static final Function<Object, Object> NEVER = _ -> null;

    private final int capacity;
    private final AtomicInteger size = new AtomicInteger(0);
    private final ConcurrentMap<K, V> cache;
    private final ArrayDeque<K> keys;
    private final Function<K, V> generator;

    /**
     * Creates a new LFUCache with the specified capacity and no value generator. If a key is
     * not present in the cache, {@code null} will be returned.
     *
     * @param capacity the maximum number of items the cache can hold
     */
    public LFUCache(int capacity) {
        this(capacity, TypeUtils.unchecked(NEVER, Function.class));
    }

    /**
     * Creates a new LFUCache with the specified capacity and value generator. If a key is not
     * present in the cache, the generator will be used to create a new value.
     *
     * @param capacity  the maximum number of items the cache can hold
     * @param generator the function to generate values for keys not present in the cache
     */
    public LFUCache(int capacity, Function<K, V> generator) {
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<>(capacity);
        this.keys = new ArrayDeque<>(capacity);
        this.generator = generator;
    }

    /**
     * Retrieves a value from the cache. If the key is not present and a generator is provided,
     * a new value will be generated, stored in the cache, and returned.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key, or {@code null} if not present and no
     * generator is provided
     */
    public V get(K key) {
        if (this.capacity == 0) {
            return this.generator.apply(key);
        }
        V value = this.cache.get(key);
        if (value == null) {
            if (this.generator == NEVER) {
                return null;
            }
            value = this.generator.apply(key);
            this.put(key, value);
        }
        else {
            // Move the key to the end of the deque to mark it as most recently used
            synchronized (this.keys) {
                this.keys.remove(key);
                this.keys.addLast(key);
            }
        }
        return value;
    }

    /**
     * Puts a key-value pair into the cache. If the cache exceeds its capacity, the least frequently
     * used item will be evicted.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    public void put(K key, V value) {
        if (this.capacity == 0) {
            return;
        }
        if (this.cache.putIfAbsent(key, value) == null) {
            synchronized (this.keys) {
                this.keys.addLast(key);
            }
            int currentSize = this.size.incrementAndGet();
            if (currentSize > this.capacity) {
                K leastUsedKey;
                synchronized (this.keys) {
                    leastUsedKey = this.keys.removeFirst();
                }
                this.cache.remove(leastUsedKey);
                this.size.decrementAndGet();
            }
        }
    }

    /**
     * Returns the current size of the cache.
     *
     * @return the number of items currently in the cache
     */
    public int size() {
        return this.size.get();
    }

    /**
     * Checks if the cache contains the specified key.
     *
     * @param key the key whose presence in the cache is to be tested
     * @return {@code true} if the cache contains the specified key, {@code false} otherwise
     */
    public boolean containsKey(K key) {
        return this.cache.containsKey(key);
    }

    /**
     * Clears all items from the cache.
     */
    public void clear() {
        this.cache.clear();
        synchronized (this.keys) {
            this.keys.clear();
        }
        this.size.set(0);
    }

    /**
     * Returns the maximum capacity of the cache.
     *
     * @return the maximum number of items the cache can hold
     */
    public int capacity() {
        return this.capacity;
    }

    /**
     * Checks if the cache is empty.
     *
     * @return {@code true} if the cache is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.size.get() == 0;
    }

    /**
     * Checks if the cache is full.
     *
     * @return {@code true} if the cache is full, {@code false} otherwise
     */
    public boolean isFull() {
        return this.size.get() >= this.capacity;
    }

    /**
     * Removes the specified key from the cache.
     *
     * @param key the key to be removed from the cache
     */
    public void remove(K key) {
        if (this.cache.remove(key) != null) {
            synchronized (this.keys) {
                this.keys.remove(key);
            }
            this.size.decrementAndGet();
        }
    }
}
