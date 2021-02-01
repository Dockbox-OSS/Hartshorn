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

package org.dockbox.selene.core.impl;

import org.dockbox.selene.core.ExceptionHelper;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The default (simple) implementation of [org.dockbox.selene.core.util.exceptions.ExceptionHelper].
 * Formats:
 * - Friendly
 * <pre>{@code
 *         Headline: java.lang.NullPointerException
 *         Message: Foo bar
 *         Location: SourceFile.java line 19
 *         Stack: [...]
 *     }</pre>
 * <p>
 * - Minimal
 * <pre>{@code
 *         NullPointerException: Foo bar
 *         Stack: [...]
 *     }</pre>
 */
public class SimpleExceptionHelper implements ExceptionHelper {

    private static final String separator = "========================================";

    @Override
    public void printFriendly(@NonNls @Nullable String message, @Nullable Throwable exception, boolean stacktrace) {
        Selene.log().error(SimpleExceptionHelper.separator);
        if (null != exception) {
            Selene.log().error("Exception: " + exception.getClass().getCanonicalName());
            if (null != message && !"".equals(message)) Selene.log().error("Message: " + message);

            if (0 < exception.getStackTrace().length) {
                StackTraceElement root = exception.getStackTrace()[0];
                String line = 0 < root.getLineNumber() ? ":" + root.getLineNumber() : "(internal call)";
                Selene.log().error("Location: " + root.getFileName() + line);

                if (stacktrace) {
                    Throwable nextException = exception;
                    while (null != nextException) {
                        StackTraceElement[] trace = nextException.getStackTrace();
                        Selene.log().error(nextException.getClass().getCanonicalName() + ": " + nextException.getMessage());
                        for (StackTraceElement element : trace) {
                            String elLine = 0 < element.getLineNumber() ? ":" + element.getLineNumber() : "(internal call)";
                            Selene.log().error("  at " + element.getClassName() + "." + element.getMethodName() + elLine);
                        }
                        nextException = nextException.getCause();
                    }
                }
            }
        }
    }

    @Override
    public void printMinimal(@NonNls @Nullable String message, @Nullable Throwable exception, boolean stacktrace) {
        Selene.log().error(SimpleExceptionHelper.separator);
        if (null != exception && null != message && !"".equals(message)) {
            Selene.log().error(exception.getClass().getSimpleName() + ": " + message);
            if (stacktrace) Selene.log().error(Arrays.toString(exception.getStackTrace()));
        }
    }

    @Override
    public void handleSafe(@NotNull Runnable runnable) {
        this.handleSafe(runnable, Selene::handle);
    }

    @Override
    public <T> void handleSafe(@NotNull Consumer<T> consumer, T value) {
        this.handleSafe(consumer, value, Selene::handle);
    }

    @Override
    @NotNull
    public <T, R> Exceptional<R> handleSafe(@NotNull Function<T, R> function, T value) {
        return this.handleSafe(function, value, Selene::handle);
    }

    @Override
    public void handleSafe(@NotNull Runnable runnable, @NotNull Consumer<Throwable> errorConsumer) {
        try {
            runnable.run();
        } catch (Throwable e) {
            errorConsumer.accept(e);
        }
    }

    @Override
    public <T> void handleSafe(@NotNull Consumer<T> consumer, T value, @NotNull Consumer<Throwable> errorConsumer) {
        try {
            consumer.accept(value);
        } catch (Throwable e) {
            errorConsumer.accept(e);
        }
    }

    @Override
    @NotNull
    public <T, R> Exceptional<R> handleSafe(@NotNull Function<T, R> function, T value, @NotNull Consumer<Throwable> errorConsumer) {
        try {
            return Exceptional.ofNullable(function.apply(value));
        } catch (Throwable e) {
            errorConsumer.accept(e);
            return Exceptional.of(e);
        }
    }
}
