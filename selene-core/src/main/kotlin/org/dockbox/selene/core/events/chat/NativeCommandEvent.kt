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

package org.dockbox.selene.core.events.chat

import org.dockbox.selene.core.annotations.Filter
import org.dockbox.selene.core.events.AbstractTargetCancellableEvent
import org.dockbox.selene.core.objects.events.Filterable
import org.dockbox.selene.core.objects.targets.CommandSource
import org.dockbox.selene.core.util.SeleneUtils
import org.dockbox.selene.core.util.events.FilterTypes

/**
 * The event fired when a command is executed natively through the implemented platform. This typically includes both
 * external commands and commands defined within Selene.
 *
 * @property alias The alias of the executed command
 * @property arguments The arguments provided in the command, split by the space character
 *
 * @param source The executing source
 */
class NativeCommandEvent(
        source: CommandSource,
        val alias: String,
        val arguments: Array<String>
) : AbstractTargetCancellableEvent(source), Filterable {

    override fun isApplicable(filter: Filter): Boolean {
        if (filter.param in arrayOf("alias", "command")) {
            return filter.type.test(filter.value, alias)
        } else if (filter.param in arrayOf("args", "arguments")) {
            val expectedArguments = filter.value.split(" ")
            if (filter.type == FilterTypes.EQUALS) {
                for (expectedArg in expectedArguments) {
                    if (!arguments.contains(expectedArg)) return false
                }
                return true

            } else if (filter.type == FilterTypes.CONTAINS) {
                for (expectedArg in expectedArguments) {
                    if (arguments.contains(expectedArg)) return true
                }
                return false
            }
        }
        return false
    }

    override fun acceptedFilters(): List<FilterTypes> {
        return FilterTypes.commonStringTypes()
    }

    override fun acceptedParams(): List<String> {
        return SeleneUtils.asUnmodifiableList("alias", "args", "arguments", "command")
    }

}
