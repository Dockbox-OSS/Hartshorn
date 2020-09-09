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

package org.dockbox.darwin.core.util.exceptions

import org.dockbox.darwin.core.server.Server
import java.util.*

class SimpleExceptionHelper : ExceptionHelper {
    override fun printFriendly(message: String?, exception: Throwable?, stacktrace: Boolean?) {
        Server.log().error("========================================")
        if (exception != null) {
            Server.log().error("Headline: " + exception.javaClass.canonicalName)
            if (message != null && "" != message) Server.log().error("Message: $message")
            if (exception.stackTrace.isNotEmpty()) {
                val root = exception.stackTrace[0]
                Server.log().error("Location: " + root.fileName + " line " + root.lineNumber)
                if (stacktrace != null && stacktrace) Server.log().error(Arrays.toString(exception.stackTrace))
            }
        } else Server.log().error("Received exception call, but exception was null")
        Server.log().error("========================================")
        // Headline: java.lang.NullPointerException
        // Message: Foo bar
        // Location: SourceFile.java line 19
        // Stack: [....]
    }

    override fun printMinimal(message: String?, exception: Throwable?, stacktrace: Boolean?) {
        Server.log().error("========================================")
        if (exception != null && message != null && "" != message) {
            Server.log().error(exception.javaClass.simpleName + ": " + message)
            if (stacktrace != null && stacktrace) Server.log().error(Arrays.toString(exception.stackTrace))
        }
        Server.log().error("========================================")
        // NullPointerException: Foo bar
        // Stack: [...]
    }
}
