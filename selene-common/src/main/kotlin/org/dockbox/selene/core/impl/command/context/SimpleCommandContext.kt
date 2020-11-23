/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.command.context

import com.sk89q.worldedit.util.command.argument.MissingArgumentException
import java.lang.reflect.Field
import java.util.*
import org.dockbox.selene.core.annotations.FromSource
import org.dockbox.selene.core.command.context.CommandContext
import org.dockbox.selene.core.command.context.CommandValue
import org.dockbox.selene.core.command.parse.AbstractTypeArgumentParser
import org.dockbox.selene.core.command.parse.rules.Rule
import org.dockbox.selene.core.command.parse.rules.Split
import org.dockbox.selene.core.command.parse.rules.Strict
import org.dockbox.selene.core.impl.command.convert.ArgumentConverter
import org.dockbox.selene.core.impl.command.convert.impl.ArgumentConverterRegistry
import org.dockbox.selene.core.impl.command.parse.DoubleArgumentParser
import org.dockbox.selene.core.impl.command.parse.EnumArgumentParser
import org.dockbox.selene.core.impl.command.parse.FloatArgumentParser
import org.dockbox.selene.core.impl.command.parse.IntegerArgumentParser
import org.dockbox.selene.core.impl.command.parse.ListArgumentParser
import org.dockbox.selene.core.impl.command.parse.LongArgumentParser
import org.dockbox.selene.core.impl.command.parse.MapArgumentParser
import org.dockbox.selene.core.impl.command.parse.ShortArgumentParser
import org.dockbox.selene.core.objects.location.Location
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.objects.optional.Exceptional
import org.dockbox.selene.core.objects.targets.CommandSource
import org.dockbox.selene.core.objects.targets.Locatable
import org.dockbox.selene.core.objects.user.Player
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.util.SeleneUtils
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@Suppress("UNCHECKED_CAST")
open class SimpleCommandContext(
        internal open val usage: String,
        internal open val args: Array<CommandValue.Argument<*>>,
        internal open val flags: Array<CommandValue.Flag<*>>,
        // Location and world are snapshots of the location of our CommandSource at the time the command was processed.
        // This way developers can ensure location data does not change while the command is being performed.
        internal open val sender: @NotNull CommandSource,
        internal open val location: @Nullable Exceptional<Location>,
        internal open val world: @Nullable Exceptional<World>,
        internal open val permissions: Array<String>
) : CommandContext {

    override val alias: String
        get() = this.usage.split(" ")[0]

    override val argumentCount: Int
        get() = args.size

    override val flagCount: Int
        get() = flags.size

    override fun getArgument(key: String): Exceptional<CommandValue.Argument<String>> {
        return Exceptional.of(Arrays.stream(args).filter { it.key == key }.findFirst()).map { CommandValue.Argument(it.value.toString(), it.key) }
    }

    override fun <T> getArgument(key: String, type: Class<T>): Exceptional<CommandValue.Argument<T>> {
        return Exceptional.of(Arrays.stream(args).filter { it.key == key }.findFirst()).map { it as CommandValue.Argument<T> }
    }

    override fun <T> getArgumentAndParse(key: String, parser: AbstractTypeArgumentParser<T>): Exceptional<T> {
        val optionalArg = getArgument(key)
        return if (optionalArg.isPresent) parser.parse(optionalArg.get())
        else Exceptional.empty()
    }

    override fun getFlag(key: String): Exceptional<CommandValue.Flag<String>> {
        return Exceptional.of(Arrays.stream(flags).filter { it.key == key }.findFirst()).map { CommandValue.Flag(it.value.toString(), it.key) }
    }

    override fun <T> getFlag(key: String, type: Class<T>): Exceptional<CommandValue.Flag<T>> {
        return Exceptional.of(Arrays.stream(flags).filter { it.key == key }.findFirst()).map { it as CommandValue.Flag<T> }
    }

    override fun <T> getFlagAndParse(key: String, parser: AbstractTypeArgumentParser<T>): Exceptional<T> {
        val optionalArg = getFlag(key)
        return if (optionalArg.isPresent) parser.parse(optionalArg.get())
        else Exceptional.empty()
    }

    override fun hasArgument(key: String): Boolean {
        return Arrays.stream(args).anyMatch { it.key == key }
    }

    override fun hasFlag(key: String): Boolean {
        return Arrays.stream(flags).anyMatch { it.key == key }
    }

    override fun <T> getValue(key: String, type: Class<T>, valType: CommandValue.Type): Exceptional<CommandValue<T>> {
        val arr = when (valType) {
            CommandValue.Type.ARGUMENT -> args as Array<CommandValue<*>>
            CommandValue.Type.FLAG -> flags as Array<CommandValue<*>>
            CommandValue.Type.BOTH -> arrayOf(*args as Array<CommandValue<*>>, *flags as Array<CommandValue<*>>)
        }
        return getValueAs(key, type, arr)
    }

    override fun <T> tryCreate(type: Class<T>): Exceptional<T> {
        try {
            val argumentKeys = this.args.map { it.key }
            val instance: T = SeleneUtils.getInstance(type)
            type.declaredFields.forEach { field ->
                run {
                    if (!argumentKeys.contains(field.name)) throwOrSetNull(field, instance)
                    else {
                        val optionalValue = tryGetValue(field).rethrow()
                        if (optionalValue.isPresent) {
                            val fieldValue = optionalValue.get()
                            field.set(instance, fieldValue)
                        } else throwOrSetNull(field, instance)
                    }
                }
            }
            return Exceptional.of(instance)
        } catch (e: Throwable) {
            return Exceptional.of(e)
        }
    }

    private fun <T> throwOrSetNull(field: Field, instance: T) {
        if ((field.isAnnotationPresent(Strict::class.java) && field.getAnnotation(Strict::class.java).strict)
                || field.isAnnotationPresent(NotNull::class.java)) {
            throw MissingArgumentException("Could not get argument value for '" + field.name + "'")
        }
        field.set(instance, null)
    }

    private fun getRawArgument(key: String): Exceptional<CommandValue.Argument<*>> {
        return Exceptional.of(Arrays.stream(args).filter { it.key == key }.findFirst()).map { CommandValue.Argument(it.value, it.key) }
    }

    private fun tryGetValue(field: Field): Exceptional<*> {
        val oa = getArgument(field.name)
        if (!oa.isPresent) return Exceptional.empty<String>()
        val arg = oa.get()
        if (field.isAnnotationPresent(FromSource::class.java)) {
            when (field.type) {
                Player::class.java -> if (this.sender is Player) return Exceptional.of(this.sender)
                World::class.java -> if (this.sender is Locatable) return Exceptional.of(this.world)
                Location::class.java -> if (this.sender is Locatable) return Exceptional.of(this.location)
                CommandSource::class.java -> return Exceptional.of(this.sender)
                else -> Selene.log().warn("Field '" + field.name + "' has FromSource annotation and type [" + field.type.canonicalName + "]")
            }
        }

        var minMax: Rule.MinMax? = null
        if (field.isAnnotationPresent(Rule.MinMax::class.java)) {
            minMax = field.getAnnotation(Rule.MinMax::class.java)
        }

        when (field.type) {
            Double::class.java -> {
                return Exceptional.of(DoubleArgumentParser().parse(arg).map {
                    if (minMax != null) when {
                        it < minMax.min -> minMax.min
                        it > minMax.max -> minMax.max
                        else -> it
                    } else it
                })
            }
            Float::class.java -> {
                return Exceptional.of(FloatArgumentParser().parse(arg).map {
                    if (minMax != null) when {
                        it < minMax.min -> minMax.min
                        it > minMax.max -> minMax.max
                        else -> it
                    } else it
                })
            }
            Integer::class.java -> {
                return Exceptional.of(IntegerArgumentParser().parse(arg).map {
                    if (minMax != null) when {
                        it < minMax.min -> minMax.min
                        it > minMax.max -> minMax.max
                        else -> it
                    } else it
                })
            }
            Long::class.java -> {
                return Exceptional.of(LongArgumentParser().parse(arg).map {
                    if (minMax != null) when {
                        it < minMax.min -> minMax.min
                        it > minMax.max -> minMax.max
                        else -> it
                    } else it
                })
            }
            Short::class.java -> {
                return Exceptional.of(ShortArgumentParser().parse(arg).map {
                    if (minMax != null) when {
                        it < minMax.min -> minMax.min
                        it > minMax.max -> minMax.max
                        else -> it
                    } else it
                })
            }

            List::class.java -> {
                val parser = ListArgumentParser<String>()
                if (field.isAnnotationPresent(Split::class.java)) parser.setDelimiter(field.getAnnotation(Split::class.java).delimiter)
                if (minMax != null) {
                    val listMinMax: ListArgumentParser.MinMax = ListArgumentParser.MinMax(minMax.min, minMax.max)
                    parser.setMinMax(listMinMax)
                }
                return Exceptional.of(parser.parse(arg))
            }

            Map::class.java -> {
                val parser = MapArgumentParser()
                if (field.isAnnotationPresent(Split::class.java)) parser.setRowDelimiter(field.getAnnotation(Split::class.java).delimiter)
                return Exceptional.of(parser.parse(arg))
            }
            SimpleCommandContext::class.java -> return Exceptional.of(this)
        }

        val roa = getRawArgument(field.name)
        if (!oa.isPresent) return Exceptional.empty<String>()
        val rarg = roa.get()

        if (rarg.value!!::class.java == field.type) {
            return Exceptional.of(rarg.value)
        }

        val converter: ArgumentConverter<*>? = ArgumentConverterRegistry.getConverter(field.type);
        if (converter != null) {
            return converter.convert(sender, arg.value)
        }

        return Exceptional.empty<String>()
    }

    private fun <T, A : CommandValue<T>> getValueAs(key: String, type: Class<T>, values: Array<CommandValue<*>>): Exceptional<A> {
        val candidate: Exceptional<CommandValue<*>> = Exceptional.of(Arrays.stream(values).filter { it.key == key }.findFirst())
        if (candidate.isPresent) {
            val commandValue: CommandValue<*> = candidate.get()
            if (commandValue.value!!.javaClass == type) return Exceptional.of(commandValue as A)
        }
        return Exceptional.empty()
    }

    companion object {
        val ENUM_ARGUMENT_PARSER: EnumArgumentParser = EnumArgumentParser()
        val EMPTY: SimpleCommandContext = SimpleCommandContext("", emptyArray(), emptyArray(), CommandSource.None, Exceptional.empty(), Exceptional.empty(), emptyArray())
    }
}
