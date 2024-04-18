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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;

/**
 * A specialized {@link ObjectContainer} for {@link ComponentCollection} instances. This container
 * acts as a composite container, in that it delegates all operations to the containers of the collection.
 *
 * @param <E> The type of the elements in the collection
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CollectionObjectContainer<E> extends AbstractObjectContainer<ComponentCollection<E>> {

    public CollectionObjectContainer(ComponentCollection<E> instance) {
        super(instance);
    }

    @Override
    public LifecycleType lifecycleType() {
        // Collections are always prototype, as their content may change
        return LifecycleType.PROTOTYPE;
    }

    @Override
    public ObjectContainer<ComponentCollection<E>> copyForObject(ComponentCollection<E> instance) {
        if (instance == this.instance()) {
            return this;
        }
        // Do not mark any of the containers as processed, that remains up to the provider to do
        // as this (collection) container does not have its own processing state
        return new CollectionObjectContainer<>(instance);
    }

    @Override
    public boolean processed() {
        return this.instance().containers().stream()
                .allMatch(ObjectContainer::processed);
    }

    @Override
    public void processed(boolean processed) {
        this.instance().containers()
                .forEach(container -> container.processed(processed));
    }
}
