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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The default (simple) implementation of {@link ExceptionHelper}. Formats:
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
@Binds(ExceptionHelper.class)
public class ExceptionHelperImpl implements ExceptionHelper {

    private static final Logger log = LoggerFactory.getLogger("Hartshorn");
    private static final String separator = "========================================";

    @Override
    public void printFriendly(@NonNls @Nullable final String message, @Nullable final Throwable exception, final boolean stacktrace) {
        log.error(ExceptionHelperImpl.separator);
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

    @Override
    public void printMinimal(@NonNls @Nullable final String message, @Nullable final Throwable exception, final boolean stacktrace) {
        log.error(ExceptionHelperImpl.separator);
        if (null != exception && null != message && !message.isEmpty()) {
            log.error(exception.getClass().getSimpleName() + ": " + message);
            if (stacktrace) log.error(Arrays.toString(exception.getStackTrace()));
        }
    }

    @Override
    public void handleSafe(@NotNull final Runnable runnable) {
        this.handleSafe(runnable, Except::handle);
    }

    @Override
    public <T> void handleSafe(@NotNull final Consumer<T> consumer, final T value) {
        this.handleSafe(consumer, value, Except::handle);
    }

    @Override
    @NotNull
    public <T, R> Exceptional<R> handleSafe(@NotNull final Function<T, R> function, final T value) {
        return this.handleSafe(function, value, Except::handle);
    }

    @Override
    public void handleSafe(@NotNull final Runnable runnable, @NotNull final Consumer<Throwable> errorConsumer) {
        try {
            runnable.run();
        }
        catch (final Throwable e) {
            errorConsumer.accept(e);
        }
    }

    @Override
    public <T> void handleSafe(@NotNull final Consumer<T> consumer, final T value, @NotNull final Consumer<Throwable> errorConsumer) {
        try {
            consumer.accept(value);
        }
        catch (final Throwable e) {
            errorConsumer.accept(e);
        }
    }

    @Override
    @NotNull
    public <T, R> Exceptional<R> handleSafe(@NotNull final Function<T, R> function, final T value, @NotNull final Consumer<Throwable> errorConsumer) {
        try {
            return Exceptional.of(function.apply(value));
        }
        catch (final Throwable e) {
            errorConsumer.accept(e);
            return Exceptional.of(e);
        }
    }
}
