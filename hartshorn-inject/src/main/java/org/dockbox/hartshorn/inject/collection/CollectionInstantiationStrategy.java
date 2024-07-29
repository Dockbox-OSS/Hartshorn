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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.collections.CollectionObjectContainer;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.inject.provider.NonTypeAwareInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.TypeAwareInstantiationStrategy;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ObjectDescriber;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A provider which provides a {@link ComponentCollection} of all the provided instances. This provider
 * should be used when backing a {@link CollectionBindingHierarchy}, and should typically be created
 * through {@link CollectionBindingHierarchy#getOrCreateInstantiationStrategy(int)}.
 *
 * <p>Note that while this type is not type-aware itself, providers that are added to this collection
 * may be. Providers will be accessed every time the collection is provided, and may be removed from
 * the collection at any time. As such, providers can apply rules for prototype or singleton behavior,
 * or any other behavior that is supported by a {@link InstantiationStrategy}.
 *
 * @param <T> the type of the components
 *
 * @see CollectionBindingHierarchy
 * @see CollectionBindingHierarchy#getOrCreateInstantiationStrategy(int)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CollectionInstantiationStrategy<T> implements NonTypeAwareInstantiationStrategy<ComponentCollection<T>> {

    private final Set<InstantiationStrategy<T>> strategies = ConcurrentHashMap.newKeySet();

    /**
     * Returns a copy of the providers that are currently part of this collection.
     *
     * @return a copy of the providers that are currently part of this collection
     */
    public Set<InstantiationStrategy<T>> providers() {
        return Set.copyOf(this.strategies);
    }

    /**
     * Adds a provider to the collection. If the provider is already present, it will not be added
     * again.
     *
     * @param strategy the provider to add
     */
    public void add(InstantiationStrategy<T> strategy) {
        this.strategies.add(strategy);
    }

    /**
     * Adds all providers to the collection. If a provider is already present, it will not be added
     * again.
     *
     * @param strategies the providers to add
     */
    public void addAll(Collection<InstantiationStrategy<T>> strategies) {
        this.strategies.addAll(strategies);
    }

    /**
     * Removes a provider from the collection. If the provider is not present, nothing will happen.
     *
     * @param strategy the provider to remove
     */
    public void remove(InstantiationStrategy<T> strategy) {
        this.strategies.remove(strategy);
    }

    /**
     * Removes all providers from the collection. If a provider is not present, nothing will happen.
     *
     * @param strategies the providers to remove
     */
    public void removeAll(Collection<InstantiationStrategy<T>> strategies) {
        this.strategies.removeAll(strategies);
    }

    /**
     * Clears all providers from the collection.
     */
    public void clear() {
        this.strategies.clear();
    }

    @Override
    public Option<ObjectContainer<ComponentCollection<T>>> provide(InjectionCapableApplication application, ComponentRequestContext requestContext) throws ApplicationException {
        Set<ObjectContainer<T>> containers = new HashSet<>();
        for(InstantiationStrategy<T> strategy : this.strategies) {
            Option<ObjectContainer<T>> container = strategy.provide(application, requestContext);
            if(container.present()) {
                containers.add(container.get());
            }
        }

        ContainerAwareComponentCollection<T> collection = new ContainerAwareComponentCollection<>(containers);
        ObjectContainer<ComponentCollection<T>> container = TypeUtils.unchecked(
                new CollectionObjectContainer<>(collection),
                ObjectContainer.class
        );
        return Option.of(container);
    }

    @Override
    public LifecycleType defaultLifecycle() {
        return LifecycleType.PROTOTYPE;
    }

    @Override
    public Tristate defaultLazy() {
        return Tristate.TRUE;
    }

    @Override
    public String toString() {
        String providers = this.strategies.stream()
                .map(provider -> provider instanceof TypeAwareInstantiationStrategy<T> typeAwareProvider
                        ? typeAwareProvider.type().getSimpleName()
                        : provider.toString()
                ).collect(Collectors.joining(", "));
        return ObjectDescriber.of(this)
                .field("providers", providers)
                .describe();
    }
}
