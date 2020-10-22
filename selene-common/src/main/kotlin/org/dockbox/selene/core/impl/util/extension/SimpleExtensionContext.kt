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

package org.dockbox.selene.core.impl.util.extension

import java.util.concurrent.ConcurrentHashMap
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.util.extension.Extension
import org.dockbox.selene.core.util.extension.ExtensionContext
import org.dockbox.selene.core.util.extension.status.ExtensionStatus

class SimpleExtensionContext(override var type: ExtensionContext.ComponentType, override var source: String, override var extensionClass: Class<*>, override var extension: Extension) : ExtensionContext {

    override var entryStatus: MutableMap<Class<*>, ExtensionStatus> = ConcurrentHashMap()

    override fun addStatus(clazz: Class<*>, status: ExtensionStatus) {
        if (status.intValue < 0) Selene.log().warn("Manually assigning deprecated status to [" + clazz.canonicalName + "]! " +
                "Deprecated statuses should only be assigned automatically based on annotation presence!")

        if (clazz.isAnnotationPresent(Deprecated::class.java)) {
            entryStatus[clazz] = ExtensionStatus.of(-status.intValue)
        }
    }

    override fun getStatus(clazz: Class<*>): ExtensionStatus? {
        return if (entryStatus.containsKey(clazz)) entryStatus[clazz] else null
    }



}
