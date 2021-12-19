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

import org.dockbox.hartshorn.core.context.ApplicationContext;

/**
 * Defines the state of the application, which is used to determine the application's behaviour through
 * {@link LifecycleObserver}s.
 *
 * @author Guus Lieben
 * @since 4.2.4
 */
public interface ApplicationState {
    /**
     * The state of the application when it is done starting up. This includes the lookup, initialization,
     * and registration of components. This state is reached when the {@link ApplicationContext} is started.
     */
    interface Started extends ApplicationState {}
}
