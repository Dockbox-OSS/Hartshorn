package org.dockbox.darwin.core.command.parse

import org.dockbox.darwin.core.command.context.CommandValue
import java.util.*

abstract class AbstractArgumentParser : AbstractParser() {
    /**
     * The method used to parse [AbstractCommandValue]s into the given
     * generic type.
     *
     * @param <A>
     * the generic type to convert to
     * @param commandValue
     * the [AbstractCommandValue] in String format to parse.
     * @return the optional type of the generic type. Should return [Optional.empty] if
     * null or if the value could not be parsed.
    </A> */
    abstract fun <A> parse(commandValue: CommandValue<String>, type: Class<A>?): Optional<A>

}
