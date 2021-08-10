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

package org.dockbox.hartshorn.api.exceptions;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public final class Except {

    private static ExceptionHandle handle = ExceptionLevels.FRIENDLY;
    private static boolean stackTraces = true;
    private Except() {
    }

    public static void useStackTraces(boolean stackTraces) {
        Except.stackTraces = stackTraces;
    }

    public static void with(ExceptionHandle handle) {
        Except.handle = handle;
    }

    public static <T> T handle(Callable<T> callable) {
        try {
            return callable.call();
        }
        catch (Exception e) {
            Except.handle(e);
            return null;
        }
    }

    public static void handle(Throwable e) {
        handle(firstMessage(e), e);
    }

    /**
     * Handles a given exception and message using the injected {@link ExceptionHelper} instance.
     *
     * @param msg
     *         The message, usually provided by the developer causing the exception. Can be null.
     * @param e
     *         Zero or more exceptions (varargs)
     */
    public static void handle(@Nullable String msg, @Nullable Throwable... e) {
        for (Throwable throwable : e) handle.handle(msg, throwable, stackTraces);
    }

    public static String firstMessage(Throwable throwable) {
        final String noCause = "No message provided";
        if (null == throwable) return noCause;
        while (true) {
            if (null != throwable.getMessage()) break;
            else if (null != throwable.getCause()) throwable = throwable.getCause();
            else break;
        }
        return null == throwable.getMessage() ? noCause : throwable.getMessage();
    }

}
