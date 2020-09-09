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

package org.dockbox.darwin.core.command.context

import com.sk89q.worldedit.util.command.argument.MissingArgumentException
import org.dockbox.darwin.core.command.parse.AbstractTypeArgumentParser
import org.dockbox.darwin.core.objects.location.Location
import org.dockbox.darwin.core.objects.location.World
import org.dockbox.darwin.core.objects.optional.Exceptional
import org.dockbox.darwin.core.objects.targets.CommandSource
import java.lang.reflect.Constructor
import java.lang.reflect.Field
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

    fun getArgument(key: String): Optional<CommandValue.Argument<String>> {
        return Arrays.stream(args).filter { it.key == key }.findFirst().map { CommandValue.Argument(it.value.toString(), it.key) }
    }

    fun <T> getArgument(key: String, type: Class<T>): Optional<CommandValue.Argument<T>> {
        return Arrays.stream(args).filter { it.key == key }.findFirst().map { it as CommandValue.Argument<T> }
    }

    fun <T> getArgumentAndParse(key: String, parser: AbstractTypeArgumentParser<T>): Optional<T> {
        val optionalArg = getArgument(key)
        return if (optionalArg.isPresent) parser.parse(optionalArg.get())
        else Optional.empty()
    }

    fun getFlag(key: String): Optional<CommandValue.Flag<String>> {
        return Arrays.stream(flags).filter { it.key == key }.findFirst().map { CommandValue.Flag(it.value.toString(), it.key) }
    }

    fun <T> getFlag(key: String, type: Class<T>): Optional<CommandValue.Flag<T>> {
        return Arrays.stream(flags).filter { it.key == key }.findFirst().map { it as CommandValue.Flag<T> }
    }

    fun <T> getFlagAndParse(key: String, parser: AbstractTypeArgumentParser<T>): Optional<T> {
        val optionalArg = getFlag(key)
        return if (optionalArg.isPresent) parser.parse(optionalArg.get())
        else Optional.empty()
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

    fun <T> tryCreate(type: Class<T>): Exceptional<T> {
        try {
            val argumentKeys = this.args!!.map { it.key }
            val ctor: Constructor<T> = type.getConstructor() as Constructor<T>
            val instance: T = ctor.newInstance()
            type.declaredFields.forEach { field ->
                run {
                    if (!argumentKeys.contains(field.name)) throw MissingArgumentException("Missing argument for '" + field.name + "'")
                    else {
                        val optionalValue = tryGetValue(field).rethrow()
                        if (optionalValue.isPresent) {
                            val fieldValue = optionalValue.get()
                            field.set(instance, fieldValue)
                        } else {
                            throw MissingArgumentException("Could not get argument value for '" + field.name + "'")
                        }
                    }
                }
            }
            return Exceptional.of(instance)
        } catch (e: Throwable) {
            return Exceptional.of(e)
        }
    }

    private fun tryGetValue(field: Field): Exceptional<Any> {
        // TODO: Create values for all minimum supported types (use AbstractArgumentParser implementations for this)
        return Exceptional.empty();
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
        val EMPTY: CommandContext = CommandContext(emptyArray(), emptyArray(), CommandSource.None, null, null, emptyArray())
    }
}
