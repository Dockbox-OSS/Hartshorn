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

package org.dockbox.selene.core.events.`object`

import org.dockbox.selene.core.annotations.Filter
import org.dockbox.selene.core.objects.events.Event
import org.dockbox.selene.core.objects.events.Filterable
import org.dockbox.selene.core.util.SeleneUtils
import org.dockbox.selene.core.util.events.FilterTypes

class ObjectConstructedEvent<T>(val type: Class<T>, val instance: T) : Event, Filterable {

    fun getTypeName(): String = type.canonicalName

    override fun isApplicable(filter: Filter): Boolean {
        return when (filter.type) {
            FilterTypes.EQUALS -> {
                filter.target == type || filter.value == type.canonicalName
            }
            else -> false
        }
    }

    override fun acceptedFilters(): List<FilterTypes> {
        return SeleneUtils.asUnmodifiableList(FilterTypes.EQUALS)
    }

    override fun acceptedParams(): List<String> {
        return SeleneUtils.asUnmodifiableList("type", "target", "class")
    }
}
