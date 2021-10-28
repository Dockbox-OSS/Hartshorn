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

package org.dockbox.hartshorn.core.exceptions;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * A utility type for easy exception handling. For all cases this should only be accessed through
 * {@link Except}. Supported formats:
 * <p><b>Friendly</b>
 * <pre>{@code
 * Headline: java.lang.NullPointerException
 * Message: Foo bar
 * Location: SourceFile.java line 19
 * Stack: [...] * }</pre>
 *
 * <p><b>Minimal</b>
 * <pre>{@code
 * NullPointerException: Foo bar
 * Stack: [...] * }</pre>
 */
public class ExceptionHelper {

    private static final Logger log = LoggerFactory.getLogger("Hartshorn");
    private static final String separator = "========================================";

    /**
     * Prints the exception in a user-friendly manner. Usually providing as much detail as possible
     * while maintaining readability.
     *
     * @param message
     *         The caught message provided by the developer
     * @param exception
     *         The exception instance
     * @param stacktrace
     *         Whether to print a stacktrace after the caught information
     */
    public static void printFriendly(@NonNls @Nullable final String message, @Nullable final Throwable exception, final boolean stacktrace) {
        log.error(ExceptionHelper.separator);
        if (null != exception) {
            log.error("Exception: " + exception.getClass().getCanonicalName());
            if (null != message && !message.isEmpty()) log.error("Message: " + message);

            if (0 < exception.getStackTrace().length) {
                final StackTraceElement root = exception.getStackTrace()[0];
                final String line = 0 < root.getLineNumber() ? ":" + root.getLineNumber() : "(internal call)";
                log.error("Location: " + root.getFileName() + line);

                if (stacktrace) {
                    Throwable nextException = exception;

                    while (null != nextException) {
                        final StackTraceElement[] trace = nextException.getStackTrace();
                        log.error(nextException.getClass().getCanonicalName() + ": " + nextException.getMessage());

                        for (final StackTraceElement element : trace) {
                            final String elLine = 0 < element.getLineNumber() ? ":" + element.getLineNumber() : "(internal call)";
                            log.error("  at " + element.getClassName() + "." + element.getMethodName() + elLine);
                        }
                        nextException = nextException.getCause();
                    }
                }
            }
        }
    }

    /**
     * Prints the exception in a minimal manner. Usually providing only the bare minimum required for
     * developers to understand what went wrong.
     *
     * @param message
     *         The caught message provided by the developer
     * @param exception
     *         The exception instance
     * @param stacktrace
     *         Whether to print a stacktrace after the caught information
     */
    public static void printMinimal(@NonNls @Nullable final String message, @Nullable final Throwable exception, final boolean stacktrace) {
        log.error(ExceptionHelper.separator);
        if (null != exception && null != message && !message.isEmpty()) {
            log.error(exception.getClass().getSimpleName() + ": " + message);
            if (stacktrace) log.error(Arrays.toString(exception.getStackTrace()));
        }
    }
}
