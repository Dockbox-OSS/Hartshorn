package org.dockbox.darwin.core.command

import org.dockbox.darwin.core.annotations.Command
import org.dockbox.darwin.core.annotations.FromSource
import org.dockbox.darwin.core.annotations.Module
import org.dockbox.darwin.core.command.context.CommandContext
import org.dockbox.darwin.core.command.registry.AbstractCommandRegistration
import org.dockbox.darwin.core.command.registry.ClassCommandRegistration
import org.dockbox.darwin.core.command.registry.MethodCommandRegistration
import org.dockbox.darwin.core.i18n.I18N
import org.dockbox.darwin.core.i18n.I18NRegistry
import org.dockbox.darwin.core.i18n.Permission
import org.dockbox.darwin.core.i18n.SimpleI18NRegistry
import org.dockbox.darwin.core.objects.location.Location
import org.dockbox.darwin.core.objects.location.World
import org.dockbox.darwin.core.objects.targets.CommandSource
import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.util.module.ModuleLoader
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
            var clazz: Class<*> = if (obj is Class<*>) obj else obj.javaClass
            Server.log().info("\n\nScanning {} for commands", clazz.toGenericString())
            try {
                if (clazz.isAnnotationPresent(Command::class.java)) registerClassCommand(clazz, obj) else registerSingleMethodCommand(clazz)
            } catch (e: Throwable) {
                Server.log().warn("Failed to register potential command class : {}", clazz.toGenericString())
                e.printStackTrace()
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
                                        if (result == null || result != "success") src.sendWithPrefix(I18N.UNKNOWN_ERROR.format(result))
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
                        if (result == null || result != "success") src.sendWithPrefix(I18N.UNKNOWN_ERROR.format(result))
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
        val information: Triple<Command, I18NRegistry, Array<String>> = getCommandInformation(clazz)
        val methods: Array<Method> = clazz.declaredMethods
        val registrations: Array<MethodCommandRegistration> = createSingleMethodRegistrations(Arrays.stream(methods).filter { it.isAnnotationPresent(Command::class.java) }.collect(Collectors.toList()))
        return ClassCommandRegistration(information.third[0], information.third, information.second, information.first, clazz, registrations)
    }

    private fun getCommandInformation(element: AnnotatedElement): Triple<Command, I18NRegistry, Array<String>> {
        val command: Command = element.getAnnotation(Command::class.java)

        val permission: I18NRegistry = if ("" == command.permissionKey) command.permission else SimpleI18NRegistry(command.permissionKey)

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
            val information: Triple<Command, Permission, Array<String>> = getCommandInformation(method)
            MethodCommandRegistration(information.third[0], information.third, information.first, method, information.second)
        }.toArray { size -> arrayOfNulls<MethodCommandRegistration>(size) }
    }

    private operator fun invoke(method: Method, sender: CommandSource, ctx: CommandContext, registration: AbstractCommandRegistration): String? {
        return try {
            val c: Class<*> = method.declaringClass
            val finalArgs: MutableList<Any> = ArrayList()
            for (parameterType in method.parameterTypes) {
                if (parameterType == CommandSource::class.java || CommandSource::class.java.isAssignableFrom(parameterType)) finalArgs.add(sender) else if (parameterType == CommandContext::class.java || CommandContext::class.java.isAssignableFrom(parameterType)) finalArgs.add(ctx) else throw IllegalStateException("Method requested parameter type '" + parameterType.toGenericString() + "' which is not provided")
            }
            val o: Any
            if (registration.sourceInstance != null) {
                o = registration.sourceInstance!!
            } else if (c == Server::class.java || c.isAssignableFrom(Server::class.java) || Server::class.java.isAssignableFrom(c)) {
                o = Server.getServer()
            } else {
                var modOptional: Optional<*>? = null
                if (c.isAnnotationPresent(Module::class.java) && Server.getInstance(ModuleLoader::class.java).getModuleInstance(c).also { modOptional = it }.isPresent) {
                    o = modOptional!!.get()
                } else {
                    o = c.getConstructor().newInstance()
                }
            }
            method.invoke(o, finalArgs.toTypedArray())
            "success" // No error message to return
        } catch (e: IllegalAccessException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            e.cause!!.message
        } catch (e: InvocationTargetException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            e.cause!!.message
        } catch (e: NoSuchMethodException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            e.cause!!.message
        } catch (e: InstantiationException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            e.cause!!.message
        } catch (e: NoSuchFieldException) {
            Server.getServer().except("Failed to invoke command", e.cause)
            e.cause!!.message
        } catch (e: Throwable) {
            e.message
        }
    }

    override fun registerCommand(command: String, permission: Permission, runner: CommandRunnerFunction) {
        if (command.indexOf(' ') < 0 && !command.startsWith("*")) registerCommandNoArgs(command, permission, runner)
        else registerCommandArgsAndOrChild(command, permission, runner)
    }

    protected fun argValue(valueString: String): A {
        var type: String?
        val key: String
        val permission: String
        val vm: Matcher = value.matcher(valueString)
        if (!vm.matches()) Server.getServer().except("Unknown argument specification `$valueString`, use Type or Name{Type} or Name{Type:Permission}")
        key = vm.group(1)
        type = vm.group(2)
        permission = vm.group(3)
        if (type == null) type = key


        return getArgumentValue(type, Permission.of(permission), key)
    }

    protected abstract fun getArgumentValue(type: String, permissions: I18NRegistry, key: String): A
    abstract fun registerCommandNoArgs(command: String, permissions: Permission, runner: CommandRunnerFunction)
    protected abstract fun convertContext(ctx: C, sender: CommandSource, command: String?): CommandContext
    abstract fun registerCommandArgsAndOrChild(command: String, permissions: Permission, runner: CommandRunnerFunction)

    companion object {
        val RegisteredCommands: List<String> = ArrayList()
        val argFinder: Pattern = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))") //each match is a flag or argument
        val flag: Pattern = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?") //g1: name  (g2: value)
        val argument: Pattern = Pattern.compile("([\\[<])(.+)[\\]>]") //g1: <[  g2: run argFinder, if nothing it's a value
        val value: Pattern = Pattern.compile("(\\w+)(?:\\{(\\w+)(?::([\\w\\.]+))?\\})?") //g1: name  g2: if present type, other wise use g1
        val subcommand: Pattern = Pattern.compile("[a-z]*")
    }
}
