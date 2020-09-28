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

interface ExceptionHelper {

    fun printFriendly(message: String?="No message provided", exception: Throwable?, stacktrace: Boolean?=true)

    fun printMinimal(message: String?="No message provided", exception: Throwable?, stacktrace: Boolean?=true)

    fun handleSafe(runnable: Runnable)
    fun <T> handleSafe(consumer: Consumer<T>, value: T)
    fun <T, R> handleSafe(function: Function<T, R>, value: T): Exceptional<R>

    fun handleSafe(runnable: Runnable, errorConsumer: Consumer<Throwable>)
    fun <T> handleSafe(consumer: Consumer<T>, value: T, errorConsumer: Consumer<Throwable>)
    fun <T, R> handleSafe(function: Function<T, R>, value: T, errorConsumer: Consumer<Throwable>): Exceptional<R>

}
