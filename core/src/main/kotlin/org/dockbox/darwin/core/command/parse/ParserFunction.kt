package org.dockbox.darwin.core.command.parse

import org.dockbox.darwin.core.command.CommandValue
import java.util.*

@FunctionalInterface
interface ParserFunction<T> {
    fun parse(value: CommandValue<String>): Optional<T>
}
