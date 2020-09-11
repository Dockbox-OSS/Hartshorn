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

package org.dockbox.darwin.core.command

import org.dockbox.darwin.core.annotations.Command
import org.dockbox.darwin.core.annotations.FromSource
import org.dockbox.darwin.core.command.context.CommandContext
import org.dockbox.darwin.core.command.registry.AbstractCommandRegistration
import org.dockbox.darwin.core.command.registry.ClassCommandRegistration
import org.dockbox.darwin.core.command.registry.MethodCommandRegistration
import org.dockbox.darwin.core.i18n.entry.IntegratedResource
import org.dockbox.darwin.core.i18n.permissions.AbstractPermission
import org.dockbox.darwin.core.i18n.permissions.ExternalPermission
import org.dockbox.darwin.core.i18n.permissions.Permission
import org.dockbox.darwin.core.objects.location.Location
import org.dockbox.darwin.core.objects.location.World
import org.dockbox.darwin.core.objects.optional.Exceptional
import org.dockbox.darwin.core.objects.targets.CommandSource
import org.dockbox.darwin.core.objects.targets.Console
import org.dockbox.darwin.core.objects.user.Player
import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.util.extension.Extension
import org.dockbox.darwin.core.util.extension.ExtensionManager
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors


abstract class SimpleCommandBus<C, A : AbstractArgumentValue<*>?> : CommandBus {
    enum class Arguments {
        BOOL, DOUBLE, ENTITY, INTEGER, LOCATION, LONG, PLAYER, MODULE, REMAININGSTRING, STRING, USER, UUID, VECTOR, WORLD, EDITSESSION, MASK, PATTERN, REGION, OTHER
    }

    override fun register(vararg objs: Any) {
        for (obj in objs) {
            val clazz: Class<*> = if (obj is Class<*>) obj else obj.javaClass
            Server.log().info("\n\nScanning {} for commands", clazz.toGenericString())
            try {
                if (clazz.isAnnotationPresent(Command::class.java)) registerClassCommand(clazz, obj) else registerSingleMethodCommand(clazz)
            } catch (e: Throwable) {
                Server.getServer().except("Failed to register potential command class : [" + clazz.canonicalName + "]", e)
            }
        }
    }

    override fun registerSingleMethodCommand(clazz: Class<*>) {
        val methods: MutableList<Method> = ArrayList<Method>()
        for (method in clazz.declaredMethods) {
            method.isAccessible = true
            if (method.isAnnotationPresent(Command::class.java)) methods.add(method)
        }
        val registrations: Array<MethodCommandRegistration> = createSingleMethodRegistrations(methods)
        Arrays.stream(registrations)
                .forEach { registration ->
                    Arrays.stream(registration.aliases)
                            .forEach { alias ->
                                val context: String = registration.command.context
                                val next = if (context.contains(" ")) context.replaceFirst(context.substring(0, context.indexOf(' ')).toRegex(), alias) else context
                                registerCommand(next, registration.permissions, object : CommandRunnerFunction {
                                    override fun run(src: CommandSource, ctx: CommandContext) {
                                        val result = invoke(registration.method, src, ctx, registration)
                                        if (result.errorPresent()) src.sendWithPrefix(IntegratedResource.UNKNOWN_ERROR.format(result.error.message))
                                    }
                                })
                                Server.log().info("Registered singular command : /$alias")
                            }
                }
    }

    override fun registerClassCommand(clazz: Class<*>, instance: Any) {
        val registration: ClassCommandRegistration = createClassRegistration(clazz)
        Arrays.stream(registration.aliases).forEach { alias ->
            if (instance !is Class<*>) registration.sourceInstance = instance

            val parentRunner = AtomicReference<CommandRunnerFunction>(object : CommandRunnerFunction {
                override fun run(src: CommandSource, ctx: CommandContext) =
                        src.sendWithPrefix("This command requires arguments!")
            })

            Arrays.stream(registration.subcommands).forEach { subRegistration ->
                val methodRunner = object : CommandRunnerFunction {
                    override fun run(src: CommandSource, ctx: CommandContext) {
                        val result = invoke(
                                subRegistration.method,
                                src, ctx,
                                subRegistration)
                        if (result.errorPresent()) src.sendWithPrefix(IntegratedResource.UNKNOWN_ERROR.format(result))
                    }
                }
                Arrays.stream(subRegistration.aliases).forEach {
                    if (it != "") {
                        val context: String = subRegistration.command.context
                        val next = if (context.contains(" ")) context.replaceFirst(context.substring(0, context.indexOf(' ')).toRegex(), alias) else context
                        registerCommand(next, subRegistration.permissions, methodRunner)
                    } else {
                        parentRunner.set(methodRunner)
                    }
                }
            }
            val context: String = registration.command.context
            val next = if (context.contains(" ")) context.replaceFirst(context.substring(0, context.indexOf(' ')).toRegex(), alias) else context
            registerCommand("*$next", registration.permissions, parentRunner.get())

            // Printing aliases, not used for actual logic
            val subcommands: MutableList<String> = ArrayList()
            Arrays.stream(registration.subcommands).forEach { subcommands.addAll(it.aliases) }
            Server.log().info("Registered command : /{} {}", alias, java.lang.String.join("|", subcommands))
        }
    }

    override fun createClassRegistration(clazz: Class<*>): ClassCommandRegistration {
        val information: Triple<Command, AbstractPermission, Array<String>> = getCommandInformation(clazz)
        val methods: Array<Method> = clazz.declaredMethods
        val registrations: Array<MethodCommandRegistration> = createSingleMethodRegistrations(Arrays.stream(methods).filter { it.isAnnotationPresent(Command::class.java) }.collect(Collectors.toList()))
        return ClassCommandRegistration(information.third[0], information.third, information.second, information.first, clazz, registrations)
    }

    private fun getCommandInformation(element: AnnotatedElement): Triple<Command, AbstractPermission, Array<String>> {
        val command: Command = element.getAnnotation(Command::class.java)

        val permission: AbstractPermission = if ("" == command.permissionKey) command.permission else ExternalPermission(command.permissionKey)

        return Triple(command, permission, command.aliases)
    }

    override fun createSingleMethodRegistrations(methods: Collection<Method>): Array<MethodCommandRegistration> {
        val commandTypes: List<Class<*>> = listOf(CommandSource::class.java, CommandContext::class.java)
        val locationTypes: List<Class<*>> = listOf(World::class.java, Location::class.java)

        return methods.stream().filter { method: Method ->
            var allowed = true
            val parameterTypes: Array<Class<*>> = method.parameterTypes
            for (type in parameterTypes) {
                val defaultTypeAssignable = AtomicBoolean(false)
                for (ct in commandTypes) {
                    if (type.isAssignableFrom(ct) || ct.isAssignableFrom(type)) {
                        defaultTypeAssignable.set(true)
                        break
                    }
                }
                val locationTypeAssignable = AtomicBoolean(false)
                for (lt in locationTypes) {
                    if (type.isAssignableFrom(lt) || lt.isAssignableFrom(type)) {
                        locationTypeAssignable.set(true)
                        break
                    }
                }
                if (!(defaultTypeAssignable.get() || type.isAnnotationPresent(FromSource::class.java) && locationTypeAssignable.get())) {
                    allowed = false
                    break
                }
            }
            allowed
        }.map { method: Method ->
            method.isAccessible = true
            val information: Triple<Command, AbstractPermission, Array<String>> = getCommandInformation(method)
            MethodCommandRegistration(information.third[0], information.third, information.first, method, information.second)
        }.toArray { size -> arrayOfNulls<MethodCommandRegistration>(size) }
    }

    private operator fun invoke(method: Method, sender: CommandSource, ctx: CommandContext, registration: AbstractCommandRegistration): Exceptional<String> {
        return try {
            val c: Class<*> = method.declaringClass
            val finalArgs: MutableList<Any> = ArrayList()

            for (parameterType in method.parameterTypes) {
                if (parameterType is CommandSource) {
                    if (parameterType == Player::class.java) {
                        if (sender is Player) finalArgs.add(sender)
                        else return Exceptional.of("skipped")
                    } else if (parameterType == Console::class.java) {
                        if (sender is Console) finalArgs.add(sender)
                        else return Exceptional.of("skipped")
                    } else finalArgs.add(sender)
                }
                else if (parameterType == CommandContext::class.java || CommandContext::class.java.isAssignableFrom(parameterType))
                    finalArgs.add(ctx)
                else
                    throw IllegalStateException("Method requested parameter type '" + parameterType.toGenericString() + "' which is not provided")
            }

            val o: Any
            if (registration.sourceInstance != null && registration.sourceInstance !is Method) {
                Server.log().info("Source instance")
                o = registration.sourceInstance!!
            } else if (c == Server::class.java || c.isAssignableFrom(Server::class.java) || Server::class.java.isAssignableFrom(c)) {
                Server.log().info("Server source")
                o = Server.getServer()
            } else {
                Server.log().info("Extension!")
                var extension: Optional<*>? = null
                if (c.isAnnotationPresent(Extension::class.java) && Server.getInstance(ExtensionManager::class.java).getInstance(c).also { extension = it }.isPresent) {
                    Server.log().info("Extension annotation present")
                    // Extension can be asserted as not-null as it is re-assigned inside the condition for instance presence
                    o = extension!!.get()
                } else {
                    Server.log().info("No extension annotation, creating instance")
                    o = c.getConstructor().newInstance()
                }
            }
            Server.log().info("Object: " + o::class.java.canonicalName)
            Server.log().info("Arguments: " + finalArgs.size)
            finalArgs.forEach { Server.log().info(" - $it") }
            method.invoke(o, *finalArgs.toTypedArray())
            Exceptional.of("success") // No error message to return
        } catch (e: IllegalAccessException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: InvocationTargetException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: NoSuchMethodException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: InstantiationException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: NoSuchFieldException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: Throwable) {
            Server.getServer().except("Failed to invoke command", e)
            Exceptional.of(e)
        }
    }

    override fun registerCommand(command: String, permission: AbstractPermission, runner: CommandRunnerFunction) {
        if (command.indexOf(' ') < 0 && !command.startsWith("*")) registerCommandNoArgs(command, permission, runner)
        else registerCommandArgsAndOrChild(command, permission, runner)
    }

    protected fun argValue(valueString: String): A {
        var type: String?
        val key: String
        val permission: String
        val vm: Matcher = value.matcher(valueString)
        if (!vm.matches()) Server.getServer().except("Unknown argument specification `$valueString`, use Type or Name{Type} or Name{Type:Permission}")
        Server.log().info("Groups: " + vm.groupCount())
        key = vm.group(1)
        type = vm.group(2)
        permission = try {
            // TODO: Prevent NPE on group 3 (permission)
            vm.group(3)
        } catch (e: NullPointerException) {
            Permission.GLOBAL_BYPASS.get()
        }
        if (type == null) type = key

        return getArgumentValue(type, Permission.of(permission), key)
    }

    protected abstract fun getArgumentValue(type: String, permissions: AbstractPermission, key: String): A
    abstract fun registerCommandNoArgs(command: String, permissions: AbstractPermission, runner: CommandRunnerFunction)
    protected abstract fun convertContext(ctx: C, sender: CommandSource, command: String?): CommandContext
    abstract fun registerCommandArgsAndOrChild(command: String, permissions: AbstractPermission, runner: CommandRunnerFunction)

    companion object {
        val RegisteredCommands: List<String> = ArrayList()
        val argFinder: Pattern = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))") //each match is a flag or argument
        val flag: Pattern = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?") //g1: name  (g2: value)
        val argument: Pattern = Pattern.compile("([\\[<])(.+)[\\]>]") //g1: <[  g2: run argFinder, if nothing it's a value
        val value: Pattern = Pattern.compile("(\\w+)(?:\\{(\\w+)(?::([\\w\\.]+))?\\})?") //g1: name  g2: if present type, other wise use g1
        val subcommand: Pattern = Pattern.compile("[a-z]*")
    }
}
