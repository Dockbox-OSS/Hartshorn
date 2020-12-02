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

import org.dockbox.selene.core.annotations.event.filter.Filter
import org.dockbox.selene.core.events.parents.Event
import org.dockbox.selene.core.events.parents.Filterable
import org.dockbox.selene.core.SeleneUtils
import org.dockbox.selene.core.events.processing.FilterTypes

/**
 * The event fired when a new type instance is constructed. Typically this only includes types which extend
 * [org.dockbox.selene.core.objects.ConstructNotifier], unless the type manually fires the event.
 *
 * @param T The type of the instance created
 * @property type The [Class] instance indicating the type of the instance
 * @property instance The created instance
 */
class ObjectConstructedEvent<T>(val type: Class<T>, val instance: T) : Event, Filterable {

    /**
     * Get the canonical name of the instance type. Typically this is only used in [ObjectConstructedEvent.isApplicable].
     *
     * @return The canonical name of the instance type.
     */
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
