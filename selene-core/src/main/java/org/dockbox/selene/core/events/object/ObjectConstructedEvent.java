/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.events.object;

import org.dockbox.selene.core.annotations.event.filter.Filter;
import org.dockbox.selene.core.events.parents.Event;
import org.dockbox.selene.core.events.parents.Filterable;
import org.dockbox.selene.core.events.processing.FilterType;
import org.dockbox.selene.core.events.processing.FilterTypes;
import org.dockbox.selene.core.util.CollectionUtil;
import org.dockbox.selene.core.util.SeleneUtils;

import java.util.List;

/**
 * The event fired when a new type instance is constructed. Typically this only includes types which extend
 * {@link org.dockbox.selene.core.objects.ConstructNotifier}, unless the type manually fires the event.
 *
 * @param <T>
 *         The type of the instance created
 */
public class ObjectConstructedEvent<T> implements Event, Filterable {

    private final Class<T> type;
    private final T instance;

    public ObjectConstructedEvent(Class<T> type, T instance) {
        this.type = type;
        this.instance = instance;
    }

    @Override
    public boolean isApplicable(Filter filter) {
        if (FilterTypes.EQUALS == filter.type()) {
            return filter.target().equals(this.type)
                    || filter.value().equals(this.getTypeName());
        }
        return false;
    }

    /**
     * Get the canonical name of the instance type. Typically this is only used in
     * {@link ObjectConstructedEvent#isApplicable}.
     *
     * @return The canonical name of the instance type.
     */
    public String getTypeName() {
        return this.type.getCanonicalName();
    }

    @Override
    public List<FilterType> acceptedFilters() {
        return SeleneUtils.COLLECTION.asUnmodifiableList(FilterTypes.EQUALS);
    }

    @Override
    public List<String> acceptedParams() {
        return SeleneUtils.COLLECTION.asUnmodifiableList("type", "target", "class");
    }
}
