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

package org.dockbox.hartshorn.inject.binding.collection;

import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.inject.CollectionEntryObjectContainer;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.util.collections.AbstractDelegatingCollection;

/**
 * A {@link ComponentCollection} that is also aware of the {@link ObjectContainer}s that were used to populate it.
 * This is mostly used for internal purposes, and should not be used directly. {@link org.dockbox.hartshorn.inject.Provider}s
 * supporting a {@link CollectionBindingHierarchy} should use this class to create a {@link ComponentCollection} instance.
 *
 * @param <T> the type of the components
 *
 * @see CollectionBindingHierarchy
 * @see CollectionProvider
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ContainerAwareComponentCollection<T> extends AbstractDelegatingCollection<T> implements ComponentCollection<T> {

    private final Set<ObjectContainer<T>> containers;

    public ContainerAwareComponentCollection(Set<ObjectContainer<T>> containers) {
        super(containers.stream()
                .map(ObjectContainer::instance)
                .collect(Collectors.toSet())
        );
        this.containers = containers.stream()
            .map(CollectionEntryObjectContainer::new)
            .collect(Collectors.toSet());
    }

    /**
     * Returns the containers that were used to populate this collection.
     *
     * @return the containers that were used to populate this collection
     */
    @Override
    public Set<ObjectContainer<T>> containers() {
        return Set.copyOf(this.containers);
    }
}
