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

package org.dockbox.selene.api.events;

import org.dockbox.selene.api.events.annotations.filter.Filter;
import org.dockbox.selene.api.events.parents.Event;
import org.dockbox.selene.api.events.parents.Filterable;
import org.dockbox.selene.api.events.processing.FilterType;
import org.dockbox.selene.api.events.processing.FilterTypes;
import org.dockbox.selene.util.SeleneUtils;

import java.util.List;

public class SampleFilterableEvent implements Event, Filterable {

    private final String name;

    public SampleFilterableEvent(String name) {
        this.name = name;
    }

    @Override
    public boolean isApplicable(Filter filter) {
        return FilterTypes.EQUALS.test(filter.value(), this.name);
    }

    @Override
    public List<FilterType> acceptedFilters() {
        return SeleneUtils.singletonList(FilterTypes.EQUALS);
    }

    @Override
    public List<String> acceptedParams() {
        return SeleneUtils.singletonList("name");
    }
}
