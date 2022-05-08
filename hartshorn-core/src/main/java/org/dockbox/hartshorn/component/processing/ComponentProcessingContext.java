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

package org.dockbox.hartshorn.component.processing;

import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultCarrierContext;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ComponentProcessingContext<O> extends DefaultCarrierContext {

    private ProcessingPhase phase;
    private final Map<Key<?>, Object> data = new ConcurrentHashMap<>();

    public ComponentProcessingContext(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public ProcessingPhase phase() {
        return this.phase;
    }

    public ComponentProcessingContext phase(final ProcessingPhase phase) {
        this.phase = phase;
        return this;
    }

    public int size() {
        return this.data.size();
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public boolean containsKey(final Key<?> key) {
        return this.data.containsKey(key);
    }

    public boolean containsValue(final Key<?> value) {
        return this.data.containsValue(value);
    }

    public <T> T get(final Key<T> key) {
        return (T) this.data.get(key);
    }

    public <T> T put(final Key<T> key, final T value) {
        return (T) this.data.put(key, value);
    }

    public <T> T remove(final Key<T> key) {
        return (T) this.data.remove(key);
    }

    public void clear() {
        this.data.clear();
    }

    public Set<Key<?>> keySet() {
        return this.data.keySet();
    }

    public Collection<Object> values() {
        return this.data.values();
    }

    public Set<Entry<Key<?>, Object>> entrySet() {
        return this.data.entrySet();
    }

    public <T> T getOrDefault(final Key<T> key, final T defaultValue) {
        return (T) this.data.getOrDefault(key, defaultValue);
    }

    public <T> T putIfAbsent(final Key<T> key, final T value) {
        return (T) this.data.putIfAbsent(key, value);
    }

    public <T> boolean remove(final Key<T> key, final T value) {
        return this.data.remove(key, value);
    }

    public <T> boolean replace(final Key<T> key, final T oldValue, final T newValue) {
        return this.data.replace(key, oldValue, newValue);
    }

    public <T> T computeIfAbsent(final Key<T> key, final Function<? super Key<T>, T> mappingFunction) {
        return (T) this.data.computeIfAbsent(key, (Function<? super Key<?>, ?>) mappingFunction);
    }
}
