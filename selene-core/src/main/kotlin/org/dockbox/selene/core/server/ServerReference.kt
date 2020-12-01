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

package org.dockbox.selene.core.server

import java.util.function.Consumer
import java.util.function.Function
import org.dockbox.selene.core.annotations.extension.Extension

abstract class ServerReference {

    open fun <T> getInstance(type: Class<T>): T {
        return Selene.getInstance(type)
    }

    open fun getExtension(type: Class<*>?): Extension? {
        // Selene uses injection mapping to access the integrated extension so Selene itself doesn't have to be
        // annotated.
        if (type == Selene::class.java)
            return getExtension(Selene.getInstance(IntegratedExtension::class.java)::class.java)
        if (type == null) return null
        return type.getAnnotation(Extension::class.java) ?: getExtension(type.superclass)
    }

    open fun <T> runWithExtension(type: Class<*>, consumer: Function<Extension, T>): T {
        val annotation = getExtension(type) ?: throw IllegalArgumentException("Requested extension is not present")
        return consumer.apply(annotation)
    }

    open fun <T, R> runWithInstance(type: Class<R>, consumer: Function<R, T>): T {
        val instance = getInstance(type) ?: throw IllegalArgumentException("Requested instance is not present")
        return consumer.apply(instance)
    }

    open fun <T> consumeWithInstance(type: Class<T>, consumer: Consumer<T>) {
        val instance = getInstance(type) ?: throw IllegalArgumentException("Requested instance is not present")
        return consumer.accept(instance)
    }
}
