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

package org.dockbox.hartshorn.inject.collection;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.binding.AbstractBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.util.option.Option;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A specialized {@link AbstractBindingHierarchy} for {@link ComponentCollection} instances. The
 * primary difference being that this hierarchy is constrained to only permit
 * {@link CollectionInstantiationStrategy} instances which can delegate to zero or more other
 * {@link InstantiationStrategy}s.
 *
 * @param <T> the type of the elements in the collection
 *
 * @author Guus Lieben
 *
 * @since 0.5.0
 */
public class CollectionBindingHierarchy<T>
        extends AbstractBindingHierarchy<ComponentCollection<T>> {

    public CollectionBindingHierarchy(ComponentKey<ComponentCollection<T>> componentKey) {
        super(componentKey);
    }

    @Override
    public CollectionBindingHierarchy<T> merge(BindingHierarchy<ComponentCollection<T>> hierarchy) {
        CollectionBindingHierarchy<T> merged = new CollectionBindingHierarchy<>(this.key());
        Map<Integer, CollectionInstantiationStrategy<T>> strategies = new HashMap<>();
        // Collect strategies from current instance
        for (Map.Entry<Integer, InstantiationStrategy<ComponentCollection<T>>> entry : this) {
            strategies.put(entry.getKey(), (CollectionInstantiationStrategy<T>) entry.getValue());
        }
        // Collect and merge strategies from given instance
        for (Map.Entry<Integer, InstantiationStrategy<ComponentCollection<T>>> entry : hierarchy) {
            if (entry.getValue() instanceof CollectionInstantiationStrategy<T> collectionStrategy) {
                Set<InstantiationStrategy<T>> providers = new HashSet<>(collectionStrategy.providers());
                if (strategies.containsKey(entry.getKey())) {
                    providers.addAll(strategies.get(entry.getKey()).providers());
                }
                // New strategy to prevent modifications to original hierarchies
                CollectionInstantiationStrategy<T> strategy = new CollectionInstantiationStrategy<>();
                strategy.addAll(providers);
                strategies.put(entry.getKey(), strategy);
            } else {
                throw new IllegalArgumentException("Only CollectionInstantiationStrategy instances can be merged into a CollectionBindingHierarchy");
            }
        }
        strategies.forEach(merged::add);
        return merged;
    }

    @Override
    public BindingHierarchy<ComponentCollection<T>> add(int priority, InstantiationStrategy<ComponentCollection<T>> strategy) {
        if (strategy instanceof CollectionInstantiationStrategy<T>) {
            return super.add(priority, strategy);
        } else {
            throw new IllegalArgumentException("Only CollectionInstantiationStrategy instances can be added to a CollectionBindingHierarchy");
        }
    }

    @Override
    public Option<CollectionInstantiationStrategy<T>> get(int priority) {
        return super.get(priority)
                .ofType(CollectionInstantiationStrategy.class)
                // Map due to type parameter
                .map(strategy -> (CollectionInstantiationStrategy<T>) strategy)
                .orCompute(() -> {
                    CollectionInstantiationStrategy<T> collectionStrategy =
                            new CollectionInstantiationStrategy<>();
                    this.add(priority, collectionStrategy);
                    return collectionStrategy;
                });
    }
}
