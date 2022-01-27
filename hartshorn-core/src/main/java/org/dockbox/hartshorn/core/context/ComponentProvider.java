/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.Enableable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import javax.inject.Named;

/**
 * A component provider is a class that is capable of providing components. Components are identified using
 * {@link Key keys}. Components can be either managed or unmanaged. Managed components are typically bound to an active
 * {@link ApplicationContext} and are therefore available to all components in the application. Unmanaged components are
 * typically not explicitly registered, and are treated as injectable beans.
 */
public interface ComponentProvider extends Context {

    /**
     * Returns the component for the given type and name metadata. If <code>named</code> is null, the given
     * {@link TypeContext} is used to identify the component.
     * @param type The type of the component to return.
     * @param named The name metadata of the component to return.
     * @param <T> The type of the component to return.
     * @return The component for the given type and name metadata.
     */
    default <T> T get(final TypeContext<T> type, final Named named) {
        return this.get(Key.of(type, named));
    }

    /**
     * Returns the component for the given type and name metadata. If <code>named</code> is null, the given
     * {@link Class} is used to identify the component.
     * @param type The type of the component to return.
     * @param named The name metadata of the component to return.
     * @param <T> The type of the component to return.
     * @return The component for the given type and name metadata.
     */
    default <T> T get(final Class<T> type, final Named named) {
        return this.get(Key.of(type, named));
    }

    /**
     * Returns the component for the given key.
     * @param key The key of the component to return.
     * @param <T> The type of the component to return.
     * @return The component for the given key.
     */
    <T> T get(Key<T> key);

    /**
     * Returns the component for the given key. Unlike {@link #get(Key)}, this method will not run {@link Enableable#enable()}
     * on the component if {@code enable} is false.
     * @param key The key of the component to return.
     * @param enable Whether to enable the component if it is an implementation of {@link Enableable}.
     * @param <T> The type of the component to return.
     * @return The component for the given key.
     */
    <T> T get(Key<T> key, boolean enable);

    /**
     * Returns the component for the given type.
     * @param type The type of the component to return.
     * @param <T> The type of the component to return.
     * @return The component for the given type.
     */
    default <T> T get(final TypeContext<T> type) {
        return this.get(Key.of(type));
    }

    /**
     * Returns the component for the given type.
     * @param type The type of the component to return.
     * @param <T> The type of the component to return.
     * @return The component for the given type.
     */
    default <T> T get(final Class<T> type) {
        return this.get(Key.of(type));
    }

}
