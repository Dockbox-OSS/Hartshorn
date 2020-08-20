package org.dockbox.darwin.core.command.parse

import org.dockbox.darwin.core.command.context.CommandValue
import java.util.*

@FunctionalInterface
interface ParserFunction<T> {
    fun parse(value: CommandValue<*>, type: Class<T>?): Optional<T>
}
