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

import org.dockbox.hartshorn.core.function.CheckedRunnable;
import org.jetbrains.annotations.Nullable;

public final class Except {

    private static ExceptionHandle handle = ExceptionLevel.FRIENDLY;
    private static boolean stackTraces = true;
    private Except() {
    }

    public static void useStackTraces(final boolean stackTraces) {
        Except.stackTraces = stackTraces;
    }

    public static void with(final ExceptionHandle handle) {
        Except.handle = handle;
    }

    public static void handle(final CheckedRunnable runnable) {
        try {
            runnable.run();
        }
        catch (final ApplicationException e) {
            Except.handle(e);
        }
    }

    public static void handle(final Throwable e) {
        handle(firstMessage(e), e);
    }

    /**
     * Handles a given exception and message using the associated exception handler for the active
     * {@link ExceptionHandle}.
     *
     * @param msg
     *         The message, usually provided by the developer causing the exception. Can be null.
     * @param e
     *         Zero or more exceptions (varargs)
     */
    public static void handle(@Nullable final String msg, @Nullable final Throwable... e) {
        for (final Throwable throwable : e) handle.handle(msg, throwable, stackTraces);
    }

    public static String firstMessage(final Throwable throwable) {
        Throwable next = throwable;
        while (next != null) {
            if (null != next.getMessage()) return next.getMessage();
            else {
                // Avoid infinitely looping if the throwable has itself as cause
                if (!next.equals(throwable.getCause())) next = next.getCause();
                else break;
            }
        }
        return "No message provided";
    }

    public static String causeMessage(final Throwable throwable) {
        String message = throwable.getMessage();
        Throwable next = throwable;
        while (next != null) {
            if (null != next.getMessage()) message = next.getMessage();
            // Avoid infinitely looping if the throwable has itself as cause
            if (!next.equals(throwable.getCause())) next = next.getCause();
            else break;
        }
        return message;
    }

}
