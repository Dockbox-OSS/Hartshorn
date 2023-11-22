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

package org.dockbox.hartshorn.inject.binding.collection;

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
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

public class ImmutableCompositeBindingHierarchy<T> implements BindingHierarchy<ComponentCollection<T>> {

    private final Set<CollectionBindingHierarchy<T>> hierarchies;
    private final ComponentKey<ComponentCollection<T>> componentKey;
    private final ApplicationContext applicationContext;

    public ImmutableCompositeBindingHierarchy(ComponentKey<ComponentCollection<T>> componentKey, ApplicationContext applicationContext,
        Set<CollectionBindingHierarchy<T>> hierarchies) {
        this.componentKey = componentKey;
        this.applicationContext = applicationContext;
        this.hierarchies = hierarchies;
    }

    @Override
    public List<Provider<ComponentCollection<T>>> providers() {
        return this.hierarchies.stream()
            .flatMap(hierarchy -> hierarchy.providers().stream())
            .collect(Collectors.toList());
    }

    @Override
    public BindingHierarchy<ComponentCollection<T>> add(Provider<ComponentCollection<T>> provider) {
        throw new UnsupportedOperationException("Cannot add to an immutable hierarchy");
    }

    @Override
    public BindingHierarchy<ComponentCollection<T>> add(int priority, Provider<ComponentCollection<T>> provider) {
        throw new UnsupportedOperationException("Cannot add to an immutable hierarchy");
    }

    @Override
    public BindingHierarchy<ComponentCollection<T>> addNext(Provider<ComponentCollection<T>> provider) {
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
    public Option<Provider<ComponentCollection<T>>> get(int priority) {
        Set<CollectionProvider<T>> providers = this.hierarchies.stream()
            .map(hierarchy -> hierarchy.getOrCreateProvider(priority))
            .collect(Collectors.toSet());
        return Option.of(new ComposedCollectionProvider<>(providers));
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
    public Iterator<Map.Entry<Integer, Provider<ComponentCollection<T>>>> iterator() {
        MultiMap<Integer, CollectionProvider<T>> providers = new ArrayListMultiMap<>();
        for (CollectionBindingHierarchy<T> hierarchy : this.hierarchies) {
            for (Map.Entry<Integer, Provider<ComponentCollection<T>>> entry : hierarchy) {
                providers.put(entry.getKey(), (CollectionProvider<T>) entry.getValue());
            }
        }

        Map<Integer, Provider<ComponentCollection<T>>> zippedProviders = new TreeMap<>(Collections.reverseOrder());
        for (int priority : providers.keySet()) {
            Collection<CollectionProvider<T>> collection = providers.get(priority);
            zippedProviders.put(priority, new ComposedCollectionProvider<>(Set.copyOf(collection)));
        }
        return zippedProviders.entrySet().iterator();
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
