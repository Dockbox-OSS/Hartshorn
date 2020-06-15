package org.dockbox.darwin.core.command.context

import org.dockbox.darwin.core.command.parse.AbstractArgumentParser
import org.dockbox.darwin.core.objects.location.Location
import org.dockbox.darwin.core.objects.location.World
import org.dockbox.darwin.core.objects.targets.CommandSource
import java.util.*

@Suppress("UNCHECKED_CAST")
open class CommandContext(
        internal open val args: Array<CommandValue.Argument<*>>?,
        internal open val flags: Array<CommandValue.Flag<*>>?,
        internal open val sender: CommandSource,
        internal open val location: Location?,
        internal open val world: World?,
        internal open val permissions: Array<String>?
) {

    val argumentCount: Int
        get() = args!!.size

    val flagCount: Int
        get() = flags!!.size

    fun <T> getArgument(key: String, type: Class<T>): Optional<CommandValue.Argument<T>> {
        return Arrays.stream(args).filter { it.key == key }.findFirst().map { it as CommandValue.Argument<T> }
    }

    fun <T> getFlag(key: String, type: Class<T>): Optional<CommandValue.Flag<T>> {
        return Arrays.stream(flags).filter { it.key == key }.findFirst().map { it as CommandValue.Flag<T> }
    }

    fun hasArgument(key: String): Boolean {
        return Arrays.stream(args).anyMatch { it.key == key }
    }

    fun hasFlag(key: String): Boolean {
        return Arrays.stream(flags).anyMatch { it.key == key }
    }

    fun <T> getValue(key: String, type: Class<T>, valType: CommandValue.Type): Optional<CommandValue<T>> {
        val arr = when (valType) {
            CommandValue.Type.ARGUMENT -> args as Array<CommandValue<*>>
            CommandValue.Type.FLAG -> flags as Array<CommandValue<*>>
            CommandValue.Type.BOTH -> arrayOf(*args as Array<CommandValue<*>>, *flags as Array<CommandValue<*>>)
        }
        return getValueAs(key, type, arr)
    }

    private fun <T, A : CommandValue<T>> getValueAs(key: String, type: Class<T>, values: Array<CommandValue<*>>): Optional<A> {
        val candidate: Optional<CommandValue<*>> = Arrays.stream(values).filter { it.key == key }.findFirst()
        if (candidate.isPresent) {
            val commandValue: CommandValue<*> = candidate.get()
            if (commandValue.value!!.javaClass == type) return Optional.of(commandValue as A)
        }
        return Optional.empty()
    }

    class EnumArgumentParser : AbstractArgumentParser() {

        override fun <A> parse(commandValue: CommandValue<String>, type: Class<A>?): Optional<A> {
            if (type!!.isEnum) {
                val enumConstants = type.enumConstants as Array<out Enum<*>>
                return Optional.ofNullable(enumConstants.first { it.name == commandValue.value }) as Optional<A>
            }
            return Optional.empty()
        }

    }

    companion object {
        val ENUM_ARGUMENT_PARSER: EnumArgumentParser = EnumArgumentParser()
    }
}
