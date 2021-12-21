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

package org.dockbox.hartshorn.core.boot;

/**
 * A lifecycle observable is an object that can be observed for lifecycle events. The lifecycle events are defined by the
 * {@link LifecycleObserver} interface.
 *
 * @author Guus Lieben
 * @since 4.2.4
 */
@FunctionalInterface
public interface LifecycleObservable {

    /**
     * Adds a lifecycle observer to this observable.
     *
     * @param observer the observer to add
     */
    void register(LifecycleObserver observer);
}
