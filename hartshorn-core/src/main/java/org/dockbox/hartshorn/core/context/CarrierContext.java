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

import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.domain.Exceptional;

/**
 * A carrier context is a context that is bound to an active {@link ApplicationContext}.
 */
public interface CarrierContext extends Context, ContextCarrier {
    /**
     * Requests the first available context of the given type. If there is no such context, the active
     * {@link ApplicationContext} is used to create a new context, if the type is annotated with {@link AutoCreating}.
     * If the context does not exist, or the type is not annotated with {@link AutoCreating}, an empty
     * {@link Exceptional} is returned.
     *
     * @param context Type The type of the context to request.
     * @param <C> Type The type of the context to request.
     * @return The requested context.
     */
    <C extends Context> Exceptional<C> first(final Class<C> context);
}
