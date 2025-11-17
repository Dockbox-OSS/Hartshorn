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

package org.dockbox.hartshorn.inject.collection;

import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.inject.provider.NonTypeAwareInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.provider.collections.CollectionObjectContainer;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.option.Option;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link InstantiationStrategy} that composes multiple {@link CollectionInstantiationStrategy}
 * instances into a single {@link ComponentCollection}. This is useful when using fuzzy search on a
 * {@link ComponentCollection} that may contain multiple components for the same key.
 *
 * @param <T> the type of the elements in the collection
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ComposedCollectionInstantiationStrategy<T> implements NonTypeAwareInstantiationStrategy<ComponentCollection<T>> {

    private final Set<CollectionInstantiationStrategy<T>> strategies;

    public ComposedCollectionInstantiationStrategy(Set<CollectionInstantiationStrategy<T>> strategies) {
        this.strategies = strategies;
    }

    @Override
    public Option<ObjectContainer<ComponentCollection<T>>> provide(InjectionCapableApplication application, ComponentRequestContext requestContext, Scope scope) throws ApplicationException {
        Set<ObjectContainer<T>> components = new HashSet<>();
        for (CollectionInstantiationStrategy<T> provider : this.strategies) {
            Option<ObjectContainer<ComponentCollection<T>>> containers = provider.provide(application, requestContext, scope);
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

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("strategies", this.strategies)
                .describe();
    }
}
