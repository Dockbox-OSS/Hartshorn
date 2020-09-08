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

package org.dockbox.darwin.core.server

import org.dockbox.darwin.core.util.extension.Extension
import java.util.function.Function

abstract class ServerReference {

    open fun <T> getInstance(type: Class<T>): T {
        return Server.getInstance(type)
    }

    open fun getExtension(type: Class<*>?): Extension? {
        if (type == null) return null;
        return type.getAnnotation(Extension::class.java) ?: getExtension(type.superclass)
    }

    open fun <T> runWithExtension(type: Class<*>, consumer: Function<Extension, T>): T {
        val annotation = getExtension(type) ?: throw IllegalArgumentException("Requested extension is not annotated as such")
        return consumer.apply(annotation)
    }
}
