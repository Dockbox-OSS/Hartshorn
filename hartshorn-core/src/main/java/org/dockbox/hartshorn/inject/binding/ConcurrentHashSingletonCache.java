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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.CustomMultiMap;
import org.dockbox.hartshorn.util.MultiMap;
import org.dockbox.hartshorn.util.Tuple;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

public class ConcurrentHashSingletonCache implements SingletonCache {

    private final MultiMap<Key<?>, Tuple<Integer, ?>> cache = new CustomMultiMap<>(() ->
            new ConcurrentSkipListSet<>(Comparator.comparingInt(t -> {
                Tuple<Integer, ?> tuple = (Tuple<Integer, ?>) t;
                return tuple.key();
            }).reversed()));

    @Override
    public <T> void put(final Key<T> key, final T instance) {
        this.put(key, instance, -1);
    }

    @Override
    public <T> void put(final Key<T> key, final T instance, final int priority) {
        this.cache.put(key, Tuple.of(priority, instance));
    }

    @Override
    public <T> T get(final Key<T> key) {
        final Collection<Tuple<Integer, ?>> entries = this.cache.get(key);
        for (Tuple<Integer, ?> entry : entries) {
            if (entry.value() != null) {
                return (T) entry.value();
            }
        }
        return null;
    }

    @Override
    public <T> void remove(final Key<T> key) {
        this.cache.remove(key);
    }

    @Override
    public <T> void remove(final Key<T> key, final int priority) {
        this.cache.get(key).removeIf(tuple -> tuple.key() == priority);
    }

    @Override
    public <T> boolean contains(final Key<T> key) {
        return this.cache.containsKey(key);
    }

    @Override
    public <T> boolean contains(final Key<T> key, final int priority) {
        if (!this.cache.containsKey(key)) {
            return false;
        }
        for (final Tuple<Integer, ?> tuple : this.cache.get(key)) {
            if (tuple.key() == priority) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> void clear() {
        this.cache.clear();
    }
}
