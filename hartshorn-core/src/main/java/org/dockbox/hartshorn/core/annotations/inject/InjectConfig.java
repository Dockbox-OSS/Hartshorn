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

package org.dockbox.hartshorn.core.annotations.inject;

import org.dockbox.hartshorn.core.InjectConfiguration;

/**
 * Used by {@link org.dockbox.hartshorn.core.annotations.activate.Activator} to add default
 * {@link InjectConfiguration}s to the application instance.
 *
 * @author Guus Lieben
 * @since 21.2
 */
public @interface InjectConfig {

    /**
     * The {@link InjectConfiguration} to add to the application instance.
     * @return The {@link InjectConfiguration} to add to the application instance.
     */
    Class<? extends InjectConfiguration> value();
}
