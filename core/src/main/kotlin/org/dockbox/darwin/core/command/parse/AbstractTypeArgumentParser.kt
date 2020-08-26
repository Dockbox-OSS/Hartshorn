package org.dockbox.darwin.core.command.parse

import org.dockbox.darwin.core.command.context.CommandValue
import java.util.*

abstract class AbstractTypeArgumentParser<T> : AbstractParser() {

    abstract fun parse(commandValue: CommandValue<String>): Optional<T>
}
