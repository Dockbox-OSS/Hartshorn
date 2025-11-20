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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A delegating {@link AliasableBindingHierarchy} that delegates all calls to the wrapped
 * {@link BindingHierarchy}, except for aliasing calls. This allows for the addition of aliases to
 * an existing hierarchy, without affecting the underlying providers.
 *
 * @param <C> The type of the component that this hierarchy is for.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public class AliasableBindingHierarchyAdapter<C> implements AliasableBindingHierarchy<C> {

    private final Set<ComponentKey<? super C>> aliases = ConcurrentHashMap.newKeySet();
    private final BindingHierarchy<C> delegate;

    public AliasableBindingHierarchyAdapter(BindingHierarchy<C> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void alias(ComponentKey<? super C> componentKey) {
        this.aliases.add(componentKey);
    }

    @Override
    public Set<ComponentKey<? super C>> aliases() {
        return Set.copyOf(this.aliases);
    }

    @Override
    public List<InstantiationStrategy<C>> providers() {
        return this.delegate.providers();
    }

    @Override
    public AliasableBindingHierarchy<C> add(InstantiationStrategy<C> strategy) {
        return this.currentOrNextAdapter(this.delegate.add(strategy));
    }

    @Override
    public AliasableBindingHierarchy<C> add(int priority, InstantiationStrategy<C> strategy) {
        return this.currentOrNextAdapter(this.delegate.add(priority, strategy));
    }

    @Override
    public AliasableBindingHierarchy<C> addNext(InstantiationStrategy<C> strategy) {
        return this.currentOrNextAdapter(this.delegate.addNext(strategy));
    }

    @Override
    public AliasableBindingHierarchy<C> merge(BindingHierarchy<C> hierarchy) {
        return this.currentOrNextAdapter(this.delegate.merge(hierarchy));
    }

    private AliasableBindingHierarchy<C> currentOrNextAdapter(BindingHierarchy<C> hierarchy) {
        if (hierarchy == this.delegate) {
            return this;
        }
        else {
            return new AliasableBindingHierarchyAdapter<>(hierarchy);
        }
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public Option<InstantiationStrategy<C>> get(int priority) {
        return this.delegate.get(priority);
    }

    @Override
    public int highestPriority() {
        return this.delegate.highestPriority();
    }

    @Override
    public SortedSet<Integer> priorities() {
        return this.delegate.priorities();
    }

    @Override
    public ComponentKey<C> key() {
        return this.delegate.key();
    }

    @Override
    public <T> boolean isCompatible(ComponentKey<T> key) {
        return this.delegate.isCompatible(key) || this.aliases.stream()
            .anyMatch(alias -> alias.equals(key));
    }

    @Override
    public Iterator<Map.Entry<Integer, InstantiationStrategy<C>>> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
            .field("aliases", this.aliases)
            .field("delegate", this.delegate)
            .describe();
    }
}
