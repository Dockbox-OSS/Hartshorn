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

package org.dockbox.selene.core.impl.command

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors
import org.dockbox.selene.core.annotations.Command
import org.dockbox.selene.core.annotations.FromSource
import org.dockbox.selene.core.command.CommandBus
import org.dockbox.selene.core.command.CommandRunnerFunction
import org.dockbox.selene.core.command.context.CommandContext
import org.dockbox.selene.core.command.registry.AbstractCommandRegistration
import org.dockbox.selene.core.command.registry.ClassCommandRegistration
import org.dockbox.selene.core.command.registry.MethodCommandRegistration
import org.dockbox.selene.core.exceptions.ConfirmFailedException
import org.dockbox.selene.core.exceptions.IllegalSourceException
import org.dockbox.selene.core.i18n.entry.IntegratedResource
import org.dockbox.selene.core.i18n.permissions.AbstractPermission
import org.dockbox.selene.core.i18n.permissions.ExternalPermission
import org.dockbox.selene.core.i18n.permissions.Permission
import org.dockbox.selene.core.impl.command.context.SimpleCommandContext
import org.dockbox.selene.core.objects.location.Location
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.objects.optional.Exceptional
import org.dockbox.selene.core.objects.targets.CommandSource
import org.dockbox.selene.core.objects.targets.Console
import org.dockbox.selene.core.objects.targets.Identifiable
import org.dockbox.selene.core.objects.tuple.Triad
import org.dockbox.selene.core.objects.user.Player
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.text.Text
import org.dockbox.selene.core.text.actions.ClickAction
import org.dockbox.selene.core.text.actions.HoverAction
import org.dockbox.selene.core.util.Utils
import org.dockbox.selene.core.util.extension.Extension
import org.dockbox.selene.core.util.extension.ExtensionManager

abstract class SimpleCommandBus<C, A : AbstractArgumentValue<*>?> : CommandBus {
    enum class Arguments {
        BOOL, DOUBLE, ENTITY, INTEGER, LOCATION, LONG, PLAYER, EXTENSION, REMAININGSTRING, STRING, USER, UUID, VECTOR, WORLD, EDITSESSION, MASK, PATTERN, REGION, OTHER
    }

    protected val parentCommandPrefix: String = "@m"

    override fun register(vararg objs: Any) {
        for (obj in objs) {
            val clazz: Class<*> = if (obj is Class<*>) obj else obj.javaClass
            Selene.log().info("Scanning {} for commands", clazz.toGenericString())
            try {
                if (clazz.isAnnotationPresent(Command::class.java)) {
                    registerClassCommand(clazz, obj)
                }
                registerSingleMethodCommands(clazz)
            } catch (e: Throwable) {
                Selene.getServer().except("Failed to register potential command class : [" + clazz.canonicalName + "]", e)
            }
        }
    }

    override fun registerSingleMethodCommands(clazz: Class<*>) {
        val methods: MutableList<Method> = ArrayList<Method>()
        for (method in clazz.declaredMethods) {
            method.isAccessible = true
            if (method.isAnnotationPresent(Command::class.java)) {
                val command = method.getAnnotation(Command::class.java)
                if (clazz.isAnnotationPresent(Command::class.java) && !command.single) continue

                methods.add(method)
            }
        }
        val registrations: Array<MethodCommandRegistration> = createSingleMethodRegistrations(methods)
        Arrays.stream(registrations)
                .forEach { registration ->
                    Arrays.stream(registration.aliases)
                            .forEach { alias ->
                                val context: String = registration.command.usage
                                val next = if (context.contains(" ")) context.replaceFirst(context.substring(0, context.indexOf(' ')).toRegex(), alias) else context
                                registerCommand(next, registration.permissions, object : CommandRunnerFunction {
                                    override fun run(src: CommandSource, ctx: CommandContext) = processRunnableCommand(registration, src, ctx)
                                })
                                Selene.log().info("Registered singular command : /$alias")
                            }
                }
    }

    private fun processRunnableCommand(registration: MethodCommandRegistration, src: CommandSource, ctx: CommandContext) {
        val runnable = Runnable {
            val result = invoke(registration.method, src, ctx, registration)
            if (result.errorPresent()) src.sendWithPrefix(IntegratedResource.UNKNOWN_ERROR.format(result.error.message))
            else if (result.isPresent) src.sendWithPrefix(result.get())
        }

        if (registration.command.requireConfirm && src is Identifiable<*>) {
            confirmableCommands[src.uniqueId] = runnable

            val confirmMessage = Text.of(IntegratedResource.CONFIRM_COMMAND_MESSAGE)
                    .onClick(ClickAction.RunCommand("/selene confirm ${src.uniqueId}"))
                    .onHover(HoverAction.ShowText(Text.of(IntegratedResource.CONFIRM_COMMAND_MESSAGE_HOVER)))

            src.sendWithPrefix(confirmMessage)
        } else runnable.run()
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
                    override fun run(src: CommandSource, ctx: CommandContext) = processRunnableCommand(subRegistration, src, ctx)
                }

                Arrays.stream(subRegistration.aliases).forEach {
                    if (it != "") {
                        // Sub commands need the parent command in the context so it can register correctly
                        val context: String = it + ' ' + subRegistration.command.usage
                        val next = if (context.contains(" ")) context.replaceFirst(context.substring(0, context.indexOf(' ')).toRegex(), alias) else context
                        registerCommand(next, subRegistration.permissions, methodRunner)
                    } else {
                        parentRunner.set(methodRunner)
                    }
                }
            }
            val context: String = registration.command.usage
            val next = if (context.contains(" ")) context.replaceFirst(context.substring(0, context.indexOf(' ')).toRegex(), alias) else alias
            registerCommand("*$next", registration.permissions, parentRunner.get())

            // Printing aliases, not used for actual logic
            val subcommands: MutableList<String> = ArrayList()
            Arrays.stream(registration.subcommands).forEach { subcommands.addAll(it.aliases) }
            Selene.log().info("Registered command : /{} {}", alias, java.lang.String.join("|", subcommands))
        }
    }

    override fun createClassRegistration(clazz: Class<*>): ClassCommandRegistration {
        val information: Triad<Command, AbstractPermission, Array<String>> = getCommandInformation(clazz)
        val methods: Array<Method> = clazz.declaredMethods
        val registrations: Array<MethodCommandRegistration> = createSingleMethodRegistrations(Arrays
                .stream(methods)
                .filter { it.isAnnotationPresent(Command::class.java) }
                .filter {
                    val command = it.getAnnotation(Command::class.java)
                    return@filter !command.single // Do not register single method commands as subcommands
                }
                .collect(Collectors.toList()))
        return ClassCommandRegistration(information.third[0], information.third, information.second, information.first, clazz, registrations)
    }

    private fun getCommandInformation(element: AnnotatedElement): Triad<Command, AbstractPermission, Array<String>> {
        val command: Command = element.getAnnotation(Command::class.java)

        val permission: AbstractPermission = if ("" == command.permissionKey) command.permission else ExternalPermission(command.permissionKey)

        return Triad(command, permission, command.aliases)
    }

    override fun createSingleMethodRegistrations(methods: Collection<Method>): Array<MethodCommandRegistration> {
        val commandTypes: List<Class<*>> = listOf(CommandSource::class.java, SimpleCommandContext::class.java)
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
            val information: Triad<Command, AbstractPermission, Array<String>> = getCommandInformation(method)
            MethodCommandRegistration(information.third[0], information.third, information.first, method, information.second)
        }.toArray { size -> arrayOfNulls<MethodCommandRegistration>(size) }
    }

    private fun checkSenderInCooldown(sender: CommandSource, ctx: CommandContext, method: Method): Boolean {
        val command = method.getAnnotation(Command::class.java)
        if (command.cooldownDuration < 0) return false
        if (sender is Identifiable<*>) {
            val registrationId = getRegistrationId(sender, ctx)
            return (Utils.isInCooldown(registrationId))
        }
        return false
    }

    private fun getRegistrationId(sender: Identifiable<*>, ctx: CommandContext): String {
        val uuid = sender.uniqueId
        val alias = ctx.alias
        return "$uuid$$alias"
    }

    private operator fun invoke(method: Method, sender: CommandSource, ctx: CommandContext, registration: AbstractCommandRegistration): Exceptional<IntegratedResource> {
        if (checkSenderInCooldown(sender, ctx, method)) {
            return Exceptional.of(IntegratedResource.IN_ACTIVE_COOLDOWN)
        }

        return try {
            val c: Class<*> = method.declaringClass
            val finalArgs: MutableList<Any> = ArrayList()

            for (parameterType in method.parameterTypes) {
                if (parameterType.isAssignableFrom(CommandSource::class.java) || CommandSource::class.java.isAssignableFrom(parameterType)) {
                    if (parameterType == Player::class.java) {
                        if (sender is Player) finalArgs.add(sender)
                        else return Exceptional.of(IllegalSourceException("Command can only be ran by players!"))
                    } else if (parameterType == Console::class.java) {
                        if (sender is Console) finalArgs.add(sender)
                        else return Exceptional.of(IllegalSourceException("Command can only be ran by the console!"))
                    } else finalArgs.add(sender)
                }
                else if (parameterType == CommandContext::class.java || CommandContext::class.java.isAssignableFrom(parameterType)) {
                    finalArgs.add(ctx)
                } else {
                    throw IllegalStateException("Method requested parameter type '" + parameterType.toGenericString() + "' which is not provided")
                }
            }

            val o: Any
            if (registration.sourceInstance != null && registration.sourceInstance !is Method) {
                o = registration.sourceInstance!!
            } else if (c == Selene::class.java || c.isAssignableFrom(Selene::class.java) || Selene::class.java.isAssignableFrom(c)) {
                o = Selene.getServer()
            } else {
                var extension: Optional<*>? = null
                if (c.isAnnotationPresent(Extension::class.java) && Selene.getInstance(ExtensionManager::class.java).getInstance(c).also { extension = it }.isPresent) {
                    // Extension can be asserted as not-null as it is re-assigned inside the condition for instance presence
                    o = extension!!.get()
                } else {
                    o = c.getConstructor().newInstance()
                }
            }

            val command = method.getAnnotation(Command::class.java)
            if (command.cooldownDuration > 0 && sender is Identifiable<*>) {
                val registrationId = getRegistrationId(sender, ctx)
                Utils.cooldown(registrationId, command.cooldownDuration, command.cooldownUnit)
            }
            method.invoke(o, *finalArgs.toTypedArray())
            Exceptional.empty() // No error message to return
        } catch (e: IllegalAccessException) {
            Selene.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: InvocationTargetException) {
            Selene.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: NoSuchMethodException) {
            Selene.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: InstantiationException) {
            Selene.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: NoSuchFieldException) {
            Selene.getServer().except("Failed to invoke command", e.cause)
            Exceptional.of(e)
        } catch (e: Throwable) {
            Selene.getServer().except("Failed to invoke command", e)
            Exceptional.of(e)
        }
    }

    override fun registerCommand(command: String, permission: AbstractPermission, runner: CommandRunnerFunction) {
        if (command.indexOf(' ') < 0 && !command.startsWith("*")) registerCommandNoArgs(command, permission, runner)
        else registerCommandArgsAndOrChild(command, permission, runner)
    }

    override fun confirmLastCommand(uuid: UUID): Exceptional<Boolean> {
        val confirmableCommandsSnapshot: Map<UUID, Runnable> = confirmableCommands
        return if (confirmableCommandsSnapshot.containsKey(uuid)) {
            val runnable = confirmableCommandsSnapshot[uuid]
            confirmableCommands.remove(uuid)
            return if (null != runnable) {
                runnable.run()
                Exceptional.of(true)
            } else {
                Exceptional.of(false, ConfirmFailedException(IntegratedResource.CONFIRM_INVALID_ENTRY.asString()))
            }
        } else Exceptional.of(false, ConfirmFailedException(IntegratedResource.CONFIRM_EXPIRED.asString()))
    }

    protected fun argValue(valueString: String): A {
        var type: String?
        val key: String
        val permission: String
        val vm: Matcher = value.matcher(valueString)
        if (!vm.matches()) Selene.getServer().except("Unknown argument specification `$valueString`, use Type or Name{Type} or Name{Type:Permission}")
        key = vm.group(1)
        type = vm.group(2)
        permission = try {
            vm.group(3)
        } catch (e: NullPointerException) {
            Permission.GLOBAL_BYPASS.get()
        }
        if (type == null) type = key

        return getArgumentValue(type, Permission.of(permission), key)
    }

    protected abstract fun getArgumentValue(type: String, permissions: AbstractPermission, key: String): A
    abstract fun registerCommandNoArgs(command: String, permissions: AbstractPermission, runner: CommandRunnerFunction)
    protected abstract fun convertContext(ctx: C, sender: CommandSource, command: String?): SimpleCommandContext

    open fun registerCommandArgsAndOrChild(command: String, permission: AbstractPermission, runner: CommandRunnerFunction) {
        Selene.log().debug(String.format("Registering command '%s' with singular permission (%s)", command, permission.get()))
        val parts = command.split(" ".toRegex()).toTypedArray()
        val part = if (1 < parts.size) parts[1] else null
        if (null != part && subcommand.matcher(part).matches()) {
            registerChildCommand(command, runner, part, permission)
        } else if (command.startsWith("*")) {
            registerParentCommand(command, runner, permission)
        } else {
            registerSingleMethodCommand(command, runner, part!!, permission)
        }
    }

    protected abstract fun registerChildCommand(command: String, runner: CommandRunnerFunction, usagePart: String, permissions: AbstractPermission)
    protected abstract fun registerSingleMethodCommand(command: String, runner: CommandRunnerFunction, usagePart: String, permissions: AbstractPermission)
    protected abstract fun registerParentCommand(command: String, runner: CommandRunnerFunction, permissions: AbstractPermission)

    companion object {
        val RegisteredCommands: List<String> = ArrayList()
        val confirmableCommands: MutableMap<UUID, Runnable> = ConcurrentHashMap()

        val argFinder: Pattern = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))") //each match is a flag or argument
        val flag: Pattern = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?") //g1: name  (g2: value)
        val argument: Pattern = Pattern.compile("([\\[<])(.+)[\\]>]") //g1: <[  g2: run argFinder, if nothing it's a value
        val value: Pattern = Pattern.compile("(\\w+)(?:\\{(\\w+)(?::([\\w\\.]+))?\\})?") //g1: name  g2: if present type, other wise use g1
        val subcommand: Pattern = Pattern.compile("[a-z]*")
    }
}
