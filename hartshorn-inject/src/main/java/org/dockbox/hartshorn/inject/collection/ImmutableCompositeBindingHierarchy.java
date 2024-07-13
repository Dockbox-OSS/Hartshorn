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

package org.dockbox.hartshorn.inject.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link BindingHierarchy} that composes multiple {@link CollectionBindingHierarchy} instances into a single hierarchy.
 * This implementation differs from {@link CollectionBindingHierarchy} in that it is immutable. It is used to represent
 * the composition of two non-strictly matching hierarchies, and are thus not expected to be bound to a key.
 *
 * @param <T> the type of the elements in the collection
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ImmutableCompositeBindingHierarchy<T> implements BindingHierarchy<ComponentCollection<T>> {

    private final Set<CollectionBindingHierarchy<T>> hierarchies;
    private final ComponentKey<ComponentCollection<T>> componentKey;

    public ImmutableCompositeBindingHierarchy(
            ComponentKey<ComponentCollection<T>> componentKey,
            Set<CollectionBindingHierarchy<T>> hierarchies
    ) {
        this.componentKey = componentKey;
        this.hierarchies = hierarchies;
    }

    @Override
    public List<InstantiationStrategy<ComponentCollection<T>>> providers() {
        return this.hierarchies.stream()
            .flatMap(hierarchy -> hierarchy.providers().stream())
            .collect(Collectors.toList());
    }

    @Override
    public BindingHierarchy<ComponentCollection<T>> add(InstantiationStrategy<ComponentCollection<T>> strategy) {
        throw new UnsupportedOperationException("Cannot add to an immutable hierarchy");
    }

    @Override
    public BindingHierarchy<ComponentCollection<T>> add(int priority, InstantiationStrategy<ComponentCollection<T>> strategy) {
        throw new UnsupportedOperationException("Cannot add to an immutable hierarchy");
    }

    @Override
    public BindingHierarchy<ComponentCollection<T>> addNext(InstantiationStrategy<ComponentCollection<T>> strategy) {
        throw new UnsupportedOperationException("Cannot add to an immutable hierarchy");
    }

    @Override
    public BindingHierarchy<ComponentCollection<T>> merge(BindingHierarchy<ComponentCollection<T>> hierarchy) {
        throw new UnsupportedOperationException("Cannot merge composite hierarchies");
    }

    @Override
    public int size() {
        return this.hierarchies.stream()
            .mapToInt(BindingHierarchy::size)
            .sum();
    }

    @Override
    public Option<InstantiationStrategy<ComponentCollection<T>>> get(int priority) {
        Set<CollectionInstantiationStrategy<T>> providers = this.hierarchies.stream()
            .map(hierarchy -> hierarchy.getOrCreateProvider(priority))
            .collect(Collectors.toSet());
        return Option.of(new ComposedCollectionInstantiationStrategy<>(providers));
    }

    @Override
    public int highestPriority() {
        return this.hierarchies.stream()
            .mapToInt(BindingHierarchy::highestPriority)
            .max()
            .orElse(-1);
    }

    @Override
    public SortedSet<Integer> priorities() {
        return this.hierarchies.stream()
            .flatMap(hierarchy -> hierarchy.priorities().stream())
            .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public ComponentKey<ComponentCollection<T>> key() {
        return this.componentKey;
    }

    @NonNull
    @Override
    public Iterator<Map.Entry<Integer, InstantiationStrategy<ComponentCollection<T>>>> iterator() {
        MultiMap<Integer, CollectionInstantiationStrategy<T>> providers = new ArrayListMultiMap<>();
        for (CollectionBindingHierarchy<T> hierarchy : this.hierarchies) {
            for (Map.Entry<Integer, InstantiationStrategy<ComponentCollection<T>>> entry : hierarchy) {
                providers.put(entry.getKey(), (CollectionInstantiationStrategy<T>) entry.getValue());
            }
        }

        Map<Integer, InstantiationStrategy<ComponentCollection<T>>> zippedProviders = new TreeMap<>(Collections.reverseOrder());
        for (int priority : providers.keySet()) {
            Collection<CollectionInstantiationStrategy<T>> collection = providers.get(priority);
            zippedProviders.put(priority, new ComposedCollectionInstantiationStrategy<>(Set.copyOf(collection)));
        }
        return zippedProviders.entrySet().iterator();
    }
}
