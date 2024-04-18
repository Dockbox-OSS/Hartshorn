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

public class CollectionEntryObjectContainer<T> extends AbstractObjectContainer<T> {

    private final ObjectContainer<T> container;

    public CollectionEntryObjectContainer(ObjectContainer<T> container) {
        super(container.instance());
        this.container = container;
    }

    @Override
    public boolean processed() {
        return this.container.processed();
    }

    @Override
    public void processed(boolean processed) {
        this.container.processed(processed);
    }

    @Override
    public LifecycleType lifecycleType() {
        return this.container.lifecycleType();
    }

    @Override
    public ObjectContainer<T> copyForObject(T instance) {
        ObjectContainer<T> copyContainer = this.container.copyForObject(instance);
        return new CollectionEntryObjectContainer<>(copyContainer);
    }

    @Override
    public boolean permitsObjectCaching() {
        // Can still be cached in provider, but should not be cached globally
        return false;
    }
}
