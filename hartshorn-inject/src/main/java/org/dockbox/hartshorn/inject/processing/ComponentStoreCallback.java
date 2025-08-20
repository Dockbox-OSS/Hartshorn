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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.util.IllegalModificationException;

/**
 * A callback interface for storing and locking components in a component store. Typically, such a store
 * is used to retain component instances during the processing of components.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface ComponentStoreCallback {

    /**
     * Stores a component in the component store, associated with the given key.
     *
     * @param key the key to associate with the component
     * @param container the container holding the component instance
     * @param <T> the type of the component being stored
     *
     * @throws IllegalModificationException if the component is already locked or if the component
     * store does not allow modifications
     */
    <T> void store(ComponentKey<T> key, ObjectContainer<T> container);

    /**
     * Locks a component in the component store, preventing further modifications to the component
     * associated with the given key.
     *
     * @param key the key of the component to lock
     * @param container the container holding the component instance
     * @param <T> the type of the component being locked
     *
     * @throws IllegalModificationException if the component is already locked or if the component
     * being locked is different from the one already stored under the key
     */
    <T> void lock(ComponentKey<T> key, ObjectContainer<T> container);
}
