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

import org.dockbox.hartshorn.core.context.ContextCarrier;

/**
 * The {@link ApplicationManager} is responsible for managing the lifecycle of the application. It combines the
 * most important parts of the application:
 * <ul>
 *     <li>The {@link ApplicationLogger}</li>
 *     <li>The {@link ApplicationProxier}</li>
 *     <li>The {@link LifecycleObservable}</li>
 *     <li>The {@link ApplicationFSProvider}</li>
 *     <li>The {@link ExceptionHandler}</li>
 * </ul>
 *
 * <p>Additionally, the manager is capable of indicating whether an application is active in a CI environment or not.
 *
 * @author Guus Lieben
 * @since 4.2.4
 */
public interface ApplicationManager extends ContextCarrier, ApplicationLogger, ApplicationProxier, LifecycleObservable, ApplicationFSProvider, ExceptionHandler {
    boolean isCI();
}
