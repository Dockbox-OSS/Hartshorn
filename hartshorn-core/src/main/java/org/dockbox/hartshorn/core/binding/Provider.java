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

package org.dockbox.hartshorn.core.binding;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

/**
 * A provider is a class that can provide an instance of a {@link Key} binding. The provider is
 * not always responsible for creating the instance, but it can be used to create the instance
 * if it is not available.
 *
 * @param <T> The type instance to provide.
 * @author Guus Lieben
 * @since 4.1.2
 */
@FunctionalInterface
public interface Provider<T> {

    /**
     * Provides an instance of the {@link Key} binding. The {@link ApplicationContext} can be used
     * to retrieve required dependencies for the instance. The context should not be used to enable
     * the instance, or populate fields of the instance.
     *
     * @param context The {@link ApplicationContext} to use.
     * @return The instance, if it can be created.
     */
    Exceptional<T> provide(ApplicationContext context);
}
