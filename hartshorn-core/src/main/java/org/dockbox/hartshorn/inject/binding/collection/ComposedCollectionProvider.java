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

import java.util.HashSet;
import java.util.Set;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.CollectionObjectContainer;
import org.dockbox.hartshorn.inject.NonTypeAwareProvider;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

public class ComposedCollectionProvider<T> implements NonTypeAwareProvider<ComponentCollection<T>> {

    private final Set<CollectionProvider<T>> providers;

    public ComposedCollectionProvider(Set<CollectionProvider<T>> providers) {
        this.providers = providers;
    }

    @Override
    public Option<ObjectContainer<ComponentCollection<T>>> provide(ApplicationContext context) throws ApplicationException {
        Set<ObjectContainer<T>> components = new HashSet<>();
        for (CollectionProvider<T> provider : this.providers) {
            Option<ObjectContainer<ComponentCollection<T>>> containers = provider.provide(context);
            if (containers.present()) {
                ComponentCollection<T> componentCollection = containers.get().instance();
                if (componentCollection instanceof ContainerAwareComponentCollection<T> containerAwareCollection) {
                    components.addAll(containerAwareCollection.containers());
                }
            }
        }
        ContainerAwareComponentCollection<T> componentCollection = new ContainerAwareComponentCollection<>(components);
        CollectionObjectContainer<ContainerAwareComponentCollection<T>, T> container = new CollectionObjectContainer<>(componentCollection);
        return Option.of(TypeUtils.adjustWildcards(container, ObjectContainer.class));
    }
}
