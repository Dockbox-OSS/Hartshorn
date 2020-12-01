/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core;

import org.dockbox.selene.core.objects.optional.Exceptional;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A low-level interface for easy exception handling. For all cases this should only be
 * accessed through {@link org.dockbox.selene.core.server.Selene}.
 */
public interface ExceptionHelper {

    /**
     * Prints the exception in a user-friendly manner. Usually providing as much detail as possible
     * while maintaining readability.
     *
     * @param message The error message provided by the developer
     * @param exception The exception instance
     * @param stacktrace Whether or not to print a stacktrace after the error information
     */
    void printFriendly(String message, Throwable exception, boolean stacktrace);

    /**
     * Prints the exception in a minimal manner. Usually providing only the bare minimum required for
     * developers to understand what went wrong.
     *
     * @param message The error message provided by the developer
     * @param exception The exception instance
     * @param stacktrace Whether or not to print a stacktrace after the error information
     */
    void printMinimal(String message, Throwable exception, boolean stacktrace);

    /**
     * Runs a given runnable, and handles any possible exceptions using default behavior for the implementation.
     * Usually this will be logging to a console or file.
     *
     * @param runnable The runnable to run
     */
    void handleSafe(Runnable runnable);

    /**
     * Runs a given consumer accepting a given value, and handles any possible exceptions using default behavior
     * for the implementation. Usually this will be logging to a console or file.
     *
     * @param <T> The type of value to be consumed
     * @param consumer The consumer to use
     * @param value The value to consume
     */
    <T> void handleSafe(Consumer<T> consumer, T value);

    /**
     * Runs a given function accepting a given value, returning a potential result wrapped in a
     * [org.dockbox.selene.core.objects.optional.Exceptional]. The exceptional should wrap the exception if
     * present. Handles any possible exceptions using default behavior for the implementation. Usually this will
     * be logging to a console or file.
     *
     * @param <T> The type of the value to be consumed
     * @param <R> The return type to be wrapped in Exceptional
     * @param function The function to use
     * @param value The value to apply to
     * @return The exceptional holding either the return type or an exception
     */
    <T,R> Exceptional<R> handleSafe(Function<T, R> function, T value);

    /**
     * Runs a given runnable, and handles any possible exceptions using a given consumer.
     *
     * @param runnable The runnable to run
     * @param errorConsumer The consumer to use for exceptions
     */
    void handleSafe(Runnable runnable, Consumer<Throwable> errorConsumer);

    /**
     * Runs a given consumer accepting a given value, and handles any possible exceptions using a given consumer.
     *
     * @param <T> The type of value to be consumed
     * @param consumer The consumer to use
     * @param value The value to consume
     * @param errorConsumer The consumer to use for exceptions
     */
    <T> void handleSafe(Consumer<T> consumer, T value, Consumer<Throwable> errorConsumer);

    /**
     * Runs a given function accepting a given value, returning a potential result wrapped in a
     * [org.dockbox.selene.core.objects.optional.Exceptional]. The exceptional should wrap the exception if
     * present. Handles any possible exceptions using a given consumer.
     *
     * @param <T> The type of the value to be consumed
     * @param <R> The return type to be wrapped in Exceptional
     * @param function The function to use
     * @param value The value to apply to
     * @param errorConsumer The consumer to use for exceptions
     * @return The exceptional holding either the return type or an exception
     */
    <T, R> Exceptional<R> handleSafe(Function<T, R> function, T value, Consumer<Throwable> errorConsumer);
}
