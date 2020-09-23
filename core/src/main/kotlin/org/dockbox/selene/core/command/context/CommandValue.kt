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

package org.dockbox.selene.core.command.context

import org.dockbox.selene.core.command.parse.ParserFunction
import java.util.*

abstract class CommandValue<T>(val value: T,
                               val key: String) {

    fun asArgument(): Argument<T> {
        return this as Argument<T>
    }

    fun asFlag(): Flag<T> {
        return this as Flag<T>
    }

    fun <P> parse(parseFunction: ParserFunction<P>, type: Class<P>): Optional<P> {
        return parseFunction.parse(this, type)
    }

    class Argument<T>(value: T, key: String) : CommandValue<T>(value, key)
    class Flag<T>(value: T, key: String) : CommandValue<T>(value, key)

    enum class Type {
        ARGUMENT, FLAG, BOTH
    }
}
