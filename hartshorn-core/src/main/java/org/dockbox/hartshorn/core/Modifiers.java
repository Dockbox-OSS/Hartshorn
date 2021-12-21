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

package org.dockbox.hartshorn.core;

/**
 * A class that contains the default modifiers for the framework. Each entry may
 * modify the behavior of the framework in a specific way.
 *
 * @author Guus Lieben
 * @since 4.1.0
 */
public enum Modifiers {
    /**
     * Makes it so application activators do not need to have service activator
     * annotationsWith present, and will indicate all activators are present when
     * requested.
     *
     * @since 4.1.0
     */
    ACTIVATE_ALL,

    /**
     * Makes it so the logging level of the application is changed to {@code DEBUG}.
     * This allows for finer logging and debugging.
     *
     * @since 4.2.5
     */
    DEBUG,
}
