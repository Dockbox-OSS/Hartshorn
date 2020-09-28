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

package org.dockbox.selene.core.util.exceptions

import java.util.function.Consumer
import java.util.function.Function
import org.dockbox.selene.core.objects.optional.Exceptional

/**
 * A low-level interface for easy exception handling. For all cases this should only be
 * accessed through [org.dockbox.selene.core.server.Selene].
 */
interface ExceptionHelper {

    /**
     * Prints the exception in a user-friendly manner. Usually providing as much detail as possible
     * while maintaining readability.
     *
     * @param message The error message provided by the developer
     * @param exception The exception instance
     * @param stacktrace Whether or not to print a stacktrace after the error information
     */
    fun printFriendly(message: String?="No message provided", exception: Throwable?, stacktrace: Boolean?=true)

    /**
     * Prints the exception in a minimal manner. Usually providing only the bare minimum required for
     * developers to understand what went wrong.
     *
     * @param message The error message provided by the developer
     * @param exception The exception instance
     * @param stacktrace Whether or not to print a stacktrace after the error information
     */
    fun printMinimal(message: String?="No message provided", exception: Throwable?, stacktrace: Boolean?=true)

    /**
     * Runs a given runnable, and handles any possible exceptions using default behavior for the implementation.
     * Usually this will be logging to a console or file.
     *
     * @param runnable The runnable to run
     */
    fun handleSafe(runnable: Runnable)

    /**
     * Runs a given consumer accepting a given value, and handles any possible exceptions using default behavior
     * for the implementation. Usually this will be logging to a console or file.
     *
     * @param T The type of value to be consumed
     * @param consumer The consumer to use
     * @param value The value to consume
     */
    fun <T> handleSafe(consumer: Consumer<T>, value: T)

    /**
     * Runs a given function accepting a given value, returning a potential result wrapped in a
     * [org.dockbox.selene.core.objects.optional.Exceptional]. The exceptional should wrap the exception if
     * present. Handles any possible exceptions using default behavior for the implementation. Usually this will
     * be logging to a console or file.
     *
     * @param T The type of the value to be consumed
     * @param R The return type to be wrapped in Exceptional
     * @param function The function to use
     * @param value The value to apply to
     * @return The exceptional holding either the return type or an exception
     */
    fun <T, R> handleSafe(function: Function<T, R>, value: T): Exceptional<R>

    /**
     * Runs a given runnable, and handles any possible exceptions using a given consumer.
     *
     * @param runnable The runnable to run
     * @param errorConsumer The consumer to use for exceptions
     */
    fun handleSafe(runnable: Runnable, errorConsumer: Consumer<Throwable>)

    /**
     * Runs a given consumer accepting a given value, and handles any possible exceptions using a given consumer.
     *
     * @param T The type of value to be consumed
     * @param consumer The consumer to use
     * @param value The value to consume
     * @param errorConsumer The consumer to use for exceptions
     */
    fun <T> handleSafe(consumer: Consumer<T>, value: T, errorConsumer: Consumer<Throwable>)

    /**
     * Runs a given function accepting a given value, returning a potential result wrapped in a
     * [org.dockbox.selene.core.objects.optional.Exceptional]. The exceptional should wrap the exception if
     * present. Handles any possible exceptions using a given consumer.
     *
     * @param T The type of the value to be consumed
     * @param R The return type to be wrapped in Exceptional
     * @param function The function to use
     * @param value The value to apply to
     * @param errorConsumer The consumer to use for exceptions
     * @return The exceptional holding either the return type or an exception
     */
    fun <T, R> handleSafe(function: Function<T, R>, value: T, errorConsumer: Consumer<Throwable>): Exceptional<R>

}
