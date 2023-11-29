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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.binding.collection.ContainerAwareComponentCollection;

/**
 * A specialized {@link ObjectContainer} for {@link ContainerAwareComponentCollection} instances. This container
 * acts as a composite container, in that it delegates all operations to the containers of the collection.
 *
 * @param <T> The type of the collection
 * @param <E> The type of the elements in the collection
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CollectionObjectContainer<T extends ContainerAwareComponentCollection<E>, E> extends ObjectContainer<T> {

    public CollectionObjectContainer(T instance) {
        super(instance);
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
