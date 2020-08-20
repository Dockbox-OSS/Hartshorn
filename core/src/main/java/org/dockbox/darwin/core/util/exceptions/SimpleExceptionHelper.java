package org.dockbox.darwin.core.util.exceptions;

import org.dockbox.darwin.core.server.CoreServer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class SimpleExceptionHelper implements ExceptionHelper {

    @Override
    public void printFriendly(@Nullable String message, @Nullable Throwable exception, @Nullable Boolean stacktrace) {
        if (exception != null) {
            CoreServer.log().error("Headline: " + exception.getClass().getCanonicalName());
            if (message != null && !"".equals(message)) CoreServer.log().error("Message: " + message);

            if (exception.getStackTrace().length > 0) {
                StackTraceElement root = exception.getStackTrace()[0];
                CoreServer.log().error("Location: " + root.getFileName() + " line " + root.getLineNumber());
                if (stacktrace != null && stacktrace) CoreServer.log().error(Arrays.toString(exception.getStackTrace()));
            }
        } else CoreServer.log().error("Received exception call, but exception was null");

        // Headline: java.lang.NullPointerException
        // Message: Foo bar
        // Location: SourceFile.java line 19
        // Stack: [....]
    }

    @Override
    public void printMinimal(@Nullable String message, @Nullable Throwable exception, @Nullable Boolean stacktrace) {
        if (exception != null && message != null && !"".equals(message)) {
            CoreServer.log().error(exception.getClass().getSimpleName() + ": " + message);
            if (stacktrace != null && stacktrace) CoreServer.log().error(Arrays.toString(exception.getStackTrace()));
        }

        // NullPointerException: Foo bar
        // Stack: [...]
    }
}
