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

import java.util.HashSet;
import java.util.Set;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.collections.CollectionObjectContainer;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.inject.provider.NonTypeAwareInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link InstantiationStrategy} that composes multiple {@link CollectionInstantiationStrategy} instances into a single {@link ComponentCollection}.
 * This is useful when using non-strict lookups on a {@link ComponentCollection} that may contain multiple components for the
 * same key.
 *
 * @param <T> the type of the elements in the collection
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ComposedCollectionInstantiationStrategy<T> implements NonTypeAwareInstantiationStrategy<ComponentCollection<T>> {

    private final Set<CollectionInstantiationStrategy<T>> providers;

    public ComposedCollectionInstantiationStrategy(Set<CollectionInstantiationStrategy<T>> providers) {
        this.providers = providers;
    }

    @Override
    public Option<ObjectContainer<ComponentCollection<T>>> provide(InjectionCapableApplication application, ComponentRequestContext requestContext) throws ApplicationException {
        Set<ObjectContainer<T>> components = new HashSet<>();
        for (CollectionInstantiationStrategy<T> provider : this.providers) {
            Option<ObjectContainer<ComponentCollection<T>>> containers = provider.provide(application, requestContext);
            if (containers.present()) {
                ComponentCollection<T> componentCollection = containers.get().instance();
                if (componentCollection instanceof ContainerAwareComponentCollection<T> containerAwareCollection) {
                    components.addAll(containerAwareCollection.containers());
                }
            }
        }
        ContainerAwareComponentCollection<T> componentCollection = new ContainerAwareComponentCollection<>(components);
        ObjectContainer<ComponentCollection<T>> container = new CollectionObjectContainer<>(componentCollection);
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
}
