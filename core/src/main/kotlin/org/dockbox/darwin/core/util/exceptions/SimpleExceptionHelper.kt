package org.dockbox.darwin.core.util.exceptions

import org.dockbox.darwin.core.server.Server
import java.util.*

class SimpleExceptionHelper : ExceptionHelper {
    override fun printFriendly(message: String?, exception: Throwable?, stacktrace: Boolean?) {
        if (exception != null) {
            Server.log().error("Headline: " + exception.javaClass.canonicalName)
            if (message != null && "" != message) Server.log().error("Message: $message")
            if (exception.stackTrace.isNotEmpty()) {
                val root = exception.stackTrace[0]
                Server.log().error("Location: " + root.fileName + " line " + root.lineNumber)
                if (stacktrace != null && stacktrace) Server.log().error(Arrays.toString(exception.stackTrace))
            }
        } else Server.log().error("Received exception call, but exception was null")

        // Headline: java.lang.NullPointerException
        // Message: Foo bar
        // Location: SourceFile.java line 19
        // Stack: [....]
    }

    override fun printMinimal(message: String?, exception: Throwable?, stacktrace: Boolean?) {
        if (exception != null && message != null && "" != message) {
            Server.log().error(exception.javaClass.simpleName + ": " + message)
            if (stacktrace != null && stacktrace) Server.log().error(Arrays.toString(exception.stackTrace))
        }

        // NullPointerException: Foo bar
        // Stack: [...]
    }
}
