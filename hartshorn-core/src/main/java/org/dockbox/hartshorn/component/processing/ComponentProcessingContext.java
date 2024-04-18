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

package org.dockbox.hartshorn.component.processing;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentProcessingContext<T> extends DefaultApplicationAwareContext {

    private final ComponentRequestContext requestContext;
    private final Map<ComponentKey<?>, Object> data;
    private final boolean permitsProxying;
    private final ComponentKey<T> key;

    protected ObjectContainer<T> container;

    public ComponentProcessingContext(
        ApplicationContext applicationContext,
        ComponentRequestContext requestContext,
        ComponentKey<T> key,
        ObjectContainer<T> container,
        boolean permitsProxying
    ) {
        super(applicationContext);
        this.requestContext = requestContext;
        this.key = key;
        this.container = container;
        this.data = new ConcurrentHashMap<>();
        this.permitsProxying = permitsProxying;
    }

    public ComponentRequestContext requestContext() {
        return this.requestContext;
    }

    public ComponentKey<T> key() {
        return this.key;
    }

    public T instance() {
        return this.container.instance();
    }

    public ObjectContainer<T> container() {
        return this.container;
    }

    public boolean permitsProxying() {
        return this.permitsProxying;
    }

    public TypeView<T> type() {
        if(this.container != null) {
            T instance = this.container.instance();
            if (instance != null) {
                return this.applicationContext().environment().introspector().introspect(instance);
            }
        }
        return this.applicationContext().environment().introspector().introspect(this.key.type());
    }

    public int size() {
        return this.data.size();
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public boolean containsKey(ComponentKey<?> key) {
        return this.data.containsKey(key);
    }

    public boolean containsKey(Class<?> type) {
        return this.containsKey(ComponentKey.of(type));
    }

    public <R> R get(ComponentKey<R> key) {
        return key.type().cast(this.data.get(key));
    }

    public <R> R get(Class<R> type) {
        return this.get(ComponentKey.of(type));
    }

    public <R> R put(ComponentKey<R> key, R value) {
        return key.type().cast(this.data.put(key, value));
    }

    public <R> R put(Class<R> type, R value) {
        return this.put(ComponentKey.of(type), value);
    }

    public <R> R remove(ComponentKey<R> key) {
        return key.type().cast(this.data.remove(key));
    }

    public <R> R remove(Class<R> type) {
        return this.remove(ComponentKey.of(type));
    }

    public void clear() {
        this.data.clear();
    }

    public Set<ComponentKey<?>> keySet() {
        return this.data.keySet();
    }

    public Collection<Object> values() {
        return this.data.values();
    }

    public Set<Entry<ComponentKey<?>, Object>> entrySet() {
        return this.data.entrySet();
    }

    public <R> R getOrDefault(ComponentKey<R> key, R defaultValue) {
        return key.type().cast(this.data.getOrDefault(key, defaultValue));
    }

    public <R> R getOrDefault(Class<R> type, R defaultValue) {
        return this.getOrDefault(ComponentKey.of(type), defaultValue);
    }

    public <R> R putIfAbsent(ComponentKey<R> key, R value) {
        return key.type().cast(this.data.putIfAbsent(key, value));
    }

    public <R> R putIfAbsent(Class<R> type, R value) {
        return this.putIfAbsent(ComponentKey.of(type), value);
    }

    public <R> boolean remove(ComponentKey<R> key, R value) {
        return this.data.remove(key, value);
    }

    public <R> boolean remove(Class<R> type, R value) {
        return this.remove(ComponentKey.of(type), value);
    }

    public <R> boolean replace(ComponentKey<R> key, R oldValue, R newValue) {
        return this.data.replace(key, oldValue, newValue);
    }

    public <R> boolean replace(Class<R> type, R oldValue, R newValue) {
        return this.replace(ComponentKey.of(type), oldValue, newValue);
    }

    public <R> R computeIfAbsent(ComponentKey<R> key, Function<? super ComponentKey<R>, R> mappingFunction) {
        return key.type().cast(this.data.computeIfAbsent(key, componentKey -> mappingFunction.apply(key)));
    }

    public <R> R computeIfAbsent(Class<R> type, Function<? super ComponentKey<R>, R> mappingFunction) {
        return this.computeIfAbsent(ComponentKey.of(type), mappingFunction);
    }
}
