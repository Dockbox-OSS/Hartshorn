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
 * A lifecycle observer is notified when the application state changes. This can be used to implement
 * application-wide logic that needs to be executed when the application starts or stops.
 *
 * @author Guus Lieben
 * @since 4.2.4
 */
public interface LifecycleObserver {
    /**
     * Called when the application is started. This is called directly after the {@link ApplicationContext}
     * has been created and configured.
     *
     * @param applicationContext The application context
     */
    void onStarted(ApplicationContext applicationContext);

    /**
     * Called when the application is stopped. This is called directly when the {@link Runtime#getRuntime() runtime}
     * is shutting down.
     *
     * @param applicationContext The application context
     */
    void onExit(ApplicationContext applicationContext);
}
