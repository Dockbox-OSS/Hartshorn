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

import org.dockbox.hartshorn.inject.binding.SingletonCache;

/**
 * A simple container for an object instance. Used to track whether an object has been processed or not.
 *
 * @param <T> the type of the object instance
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public interface ObjectContainer<T> {

    /**
     * Returns the object instance. Note that this instance may or may not have been processed.
     *
     * @return the object instance
     */
    T instance();

    /**
     * Returns whether the object instance has been processed or not.
     *
     * @return {@code true} if the object instance has been processed, {@code false} otherwise
     */
    boolean processed();

    /**
     * Sets whether the object instance has been processed or not. This method is intended to be used by
     * providers that are responsible for processing the object instance.
     *
     * @param processed whether the object instance has been processed or not
     */
    void processed(boolean processed);

    LifecycleType lifecycleType();

    /**
     * Returns whether the object represented by this container can be cached. This method is used by the
     * component provider to store objects in case they may be re-used. A common example of this is a singleton
     * being stored in the scope's {@link SingletonCache}.
     *
     * <p>Defaults to {@code true}, but may be overridden by implementations.
     *
     * @return {@code true} if the object can be cached, {@code false} otherwise
     */
    default boolean permitsObjectCaching() {
        return true;
    }

    ObjectContainer<T> copyForObject(T instance);
}
