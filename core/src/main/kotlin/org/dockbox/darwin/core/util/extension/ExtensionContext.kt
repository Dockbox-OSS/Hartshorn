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

package org.dockbox.darwin.core.util.extension

import java.util.concurrent.ConcurrentHashMap
import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.util.extension.status.ExtensionStatus

class ExtensionContext(var type: ComponentType, var source: String) {

    var entryStatus: MutableMap<Class<*>, ExtensionStatus> = ConcurrentHashMap()
    var classes: MutableMap<Extension, Class<*>> = ConcurrentHashMap()

    fun addComponentClass(clazz: Class<*>): Boolean {
        if (clazz.isAnnotationPresent(Extension::class.java)) {
            val header = clazz.getAnnotation(Extension::class.java)
            classes[header] = clazz
            return true
        }
        return false
    }

    fun addStatus(clazz: Class<*>, status: ExtensionStatus) {
        if (status.intValue < 0) Server.log().warn("Manually assigning deprecated status to [" + clazz.canonicalName + "]! " +
                "Deprecated statuses should only be assigned automatically based on annotation presence!")

        if (clazz.isAnnotationPresent(Deprecated::class.java)) {
            entryStatus[clazz] = ExtensionStatus.of(-status.intValue)
        }
    }

    fun getStatus(clazz: Class<*>): ExtensionStatus? {
        return if (entryStatus.containsKey(clazz)) entryStatus[clazz] else null
    }

    enum class ComponentType(var string: String) {
        EXTERNAL_JAR("External .jar file"), INTERNAL_CLASS("Internal class"), UNKNOWN("Unknown")
    }

}
