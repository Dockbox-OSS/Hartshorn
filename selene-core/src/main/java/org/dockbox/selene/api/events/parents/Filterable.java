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

package org.dockbox.selene.api.events.parents;

import org.dockbox.selene.api.annotations.event.filter.Filter;
import org.dockbox.selene.api.events.processing.FilterType;

import java.util.List;

/** A low level type which accepts a {@link Filter} to be applied to it. */
public interface Filterable {

    /**
     * Indicates whether or not the implementation and its properties match a given {@link Filter}.
     *
     * @param filter
     *         The filter to apply
     *
     * @return Whether or not the filter applies
     */
    boolean isApplicable(Filter filter);

    /**
     * Gets the list of accepted {@link FilterType} for the implementation
     *
     * @return The list of {@link FilterType}
     */
    List<FilterType> acceptedFilters();

    /**
     * Gets the list of accepts parameters for the implementation. Typically this includes aliases for
     * several different parameters.
     *
     * @return The accepted parameters
     */
    List<String> acceptedParams();
}
