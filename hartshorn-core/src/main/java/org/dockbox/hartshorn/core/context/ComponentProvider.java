/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import javax.inject.Named;

/**
 * A component provider is a class that is capable of providing components. Components are identified using
 * {@link Key keys}. Components can be either managed or unmanaged. Managed components are typically bound to an active
 * {@link ApplicationContext} and are therefore available to all components in the application. Unmanaged components are
 * typically not explicitely registered, and are treated as injectable beans.
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
