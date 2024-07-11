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

import org.dockbox.hartshorn.inject.provider.collections.CollectionObjectContainer;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.inject.provider.NonTypeAwareProvider;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.provider.Provider;
import org.dockbox.hartshorn.inject.provider.TypeAwareProvider;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ObjectDescriber;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A provider which provides a {@link ComponentCollection} of all the provided instances. This provider
 * should be used when backing a {@link CollectionBindingHierarchy}, and should typically be created
 * through {@link CollectionBindingHierarchy#getOrCreateProvider(int)}.
 *
 * <p>Note that while this type is not type-aware itself, providers that are added to this collection
 * may be. Providers will be accessed every time the collection is provided, and may be removed from
 * the collection at any time. As such, providers can apply rules for prototype or singleton behavior,
 * or any other behavior that is supported by a {@link Provider}.
 *
 * @param <T> the type of the components
 *
 * @see CollectionBindingHierarchy
 * @see CollectionBindingHierarchy#getOrCreateProvider(int)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CollectionProvider<T> implements NonTypeAwareProvider<ComponentCollection<T>> {

    private final Set<Provider<T>> providers = ConcurrentHashMap.newKeySet();

    /**
     * Returns a copy of the providers that are currently part of this collection.
     *
     * @return a copy of the providers that are currently part of this collection
     */
    public Set<Provider<T>> providers() {
        return Set.copyOf(this.providers);
    }

    /**
     * Adds a provider to the collection. If the provider is already present, it will not be added
     * again.
     *
     * @param provider the provider to add
     */
    public void add(Provider<T> provider) {
        this.providers.add(provider);
    }

    /**
     * Adds all providers to the collection. If a provider is already present, it will not be added
     * again.
     *
     * @param providers the providers to add
     */
    public void addAll(Collection<Provider<T>> providers) {
        this.providers.addAll(providers);
    }

    /**
     * Removes a provider from the collection. If the provider is not present, nothing will happen.
     *
     * @param provider the provider to remove
     */
    public void remove(Provider<T> provider) {
        this.providers.remove(provider);
    }

    /**
     * Removes all providers from the collection. If a provider is not present, nothing will happen.
     *
     * @param providers the providers to remove
     */
    public void removeAll(Collection<Provider<T>> providers) {
        this.providers.removeAll(providers);
    }

    /**
     * Clears all providers from the collection.
     */
    public void clear() {
        this.providers.clear();
    }

    @Override
    public Option<ObjectContainer<ComponentCollection<T>>> provide(ComponentRequestContext requestContext) throws ApplicationException {
        Set<ObjectContainer<T>> containers = new HashSet<>();
        for(Provider<T> provider : this.providers) {
            Option<ObjectContainer<T>> container = provider.provide(requestContext);
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
        String providers = this.providers.stream()
                .map(provider -> provider instanceof TypeAwareProvider<T> typeAwareProvider
                        ? typeAwareProvider.type().getSimpleName()
                        : provider.toString()
                ).collect(Collectors.joining(", "));
        return ObjectDescriber.of(this)
                .field("providers", providers)
                .describe();
    }
}
