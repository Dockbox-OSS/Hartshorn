package org.dockbox.darwin.core.command.context

import org.dockbox.darwin.core.command.parse.ParserFunction
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
