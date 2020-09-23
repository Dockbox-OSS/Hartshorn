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

package org.dockbox.selene.core.impl.command.parse

import org.dockbox.selene.core.command.context.CommandValue
import org.dockbox.selene.core.command.parse.AbstractTypeArgumentParser
import org.dockbox.selene.core.i18n.common.ResourceEntry
import org.dockbox.selene.core.i18n.common.ResourceService
import org.dockbox.selene.core.i18n.entry.IntegratedResource
import org.dockbox.selene.core.server.Selene
import java.util.*

class ResourceArgumentParser : AbstractTypeArgumentParser<ResourceEntry>() {

    override fun parse(commandValue: CommandValue<String>): Optional<ResourceEntry> {
        var k = commandValue.value
        val rs = Selene.getInstance(ResourceService::class.java)
        k = rs.createValidKey(k)

        val or = rs.getExternalResource(k)
        if (or.isPresent) return or.map { it as ResourceEntry }

        return try {
            Optional.of(IntegratedResource.valueOf(k))
        } catch (e: IllegalArgumentException) {
            Optional.empty()
        }
    }
}
