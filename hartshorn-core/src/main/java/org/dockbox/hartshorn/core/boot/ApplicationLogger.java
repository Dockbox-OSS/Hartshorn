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

import org.dockbox.hartshorn.core.annotations.context.LogExclude;
import org.slf4j.Logger;

/**
 * The {@link ApplicationLogger} is a wrapper for the {@link Logger} class. This allows for modification and validation
 * of the logger used throughout an active application.
 *
 * @author Guus Lieben
 * @since 21.9
 */
@FunctionalInterface
@LogExclude
public interface ApplicationLogger {

    /**
     * Gets the logger.
     * @return The logger.
     */
    Logger log();
}
