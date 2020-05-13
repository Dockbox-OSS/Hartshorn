package com.darwinreforged.server.core.commands;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.commands.annotations.Permission;
import com.darwinreforged.server.core.commands.annotations.Source;
import com.darwinreforged.server.core.commands.context.CommandArgument;
import com.darwinreforged.server.core.commands.context.CommandContext;
import com.darwinreforged.server.core.commands.context.CommandFlag;
import com.darwinreforged.server.core.commands.registrations.ClassRegistration;
import com.darwinreforged.server.core.commands.registrations.CommandRegistration;
import com.darwinreforged.server.core.commands.registrations.SingleMethodRegistration;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.types.internal.Singleton;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.types.tuple.Triple;
import com.darwinreforged.server.core.types.tuple.Tuple;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 The type Command bus.
 */
public class CommandBus {

    /**
     The constant COMMANDS.
     */
    protected static final Map<String, CommandRegistration> COMMANDS = new HashMap<>();

    /**
     Register.

     @param obj
     the obj
     */
    public void register(Class<?>... obj) {
        for (Class<?> clazz : obj) {
            DarwinServer.getLog().info(String.format("\n\nScanning %s for commands", clazz.toGenericString()));
            try {
                if (clazz.isAnnotationPresent(Command.class)) {
                    ClassRegistration registration = handleClassType(clazz);
                    Arrays.stream(registration.getAliases()).forEach(alias -> {
                        COMMANDS.put(alias, registration);
                        DarwinServer.getLog().info(String.format("Registered command : /%s", alias));
                    });
                } else {
                    List<Method> methods = new ArrayList<>();
                    for (Method method : clazz.getDeclaredMethods()) {
                        method.setAccessible(true);
                        if (method.isAnnotationPresent(Command.class)) methods.add(method);
                    }
                    SingleMethodRegistration[] registrations = handleSingleMethod(methods);
                    DarwinServer.getLog().info(String.format("Found %d registrations in %s", registrations.length, clazz.toGenericString()));
                    Arrays.stream(registrations)
                            .forEach(registration -> Arrays.stream(registration.getAliases())
                                    .forEach(alias -> {
                                        COMMANDS.put(alias, registration);
                                        DarwinServer.getLog().info("Registered command : /" + alias);
                                    }));
                }
            } catch (Throwable e) {
                DarwinServer.getLog().warn(String.format("Failed to register potential command class : %s", clazz.toGenericString()));
                e.printStackTrace();
            }
        }
    }

    private ClassRegistration handleClassType(Class<?> clazz) {
        Triple<Command, Permissions[], String[]> information = getCommandInformation(clazz);
        Method[] methods = clazz.getDeclaredMethods();
        SingleMethodRegistration[] registrations = handleSingleMethod(Arrays.stream(methods).filter(m -> m.isAnnotationPresent(Command.class)).collect(Collectors.toList()));
        return new ClassRegistration(information.getThird()[0], information.getThird(), information.getSecond(), information.getFirst(), clazz, registrations);
    }

    private Triple<Command, Permissions[], String[]> getCommandInformation(AnnotatedElement element) {
        Command command = element.getAnnotation(Command.class);
        Permissions[] permissions = new Permissions[]{Permissions.ADMIN_BYPASS};
        if (element.isAnnotationPresent(Permission.class)) {
            Permission permission = element.getAnnotation(Permission.class);
            permissions = permission.value();
        }
        return new Triple<>(command, permissions, command.aliases());
    }

    private SingleMethodRegistration[] handleSingleMethod(Collection<Method> methods) {
        List<Class<?>> commandTypes = Arrays.asList(CommandSender.class, CommandContext.class);
        List<Class<?>> locationTypes = Arrays.asList(DarwinWorld.class, DarwinLocation.class);

        return methods.stream().filter(method -> {
            boolean allowed = true;

            Command command = method.getAnnotation(Command.class);
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> type : parameterTypes) {
                AtomicBoolean defaultTypeAssignable = new AtomicBoolean(false);
                for (Class<?> ct : commandTypes) {
                    if (type.isAssignableFrom(ct) || ct.isAssignableFrom(type)) {
                        defaultTypeAssignable.set(true);
                        break;
                    }
                }

                AtomicBoolean locationTypeAssignable = new AtomicBoolean(false);
                for (Class<?> lt : locationTypes) {
                    if (type.isAssignableFrom(lt)  || lt.isAssignableFrom(type)) {
                        locationTypeAssignable.set(true);
                        break;
                    }
                }

                if (!(defaultTypeAssignable.get() || (type.isAnnotationPresent(Source.class) && command.injectLocations() && locationTypeAssignable.get()))) {
                    allowed = false;
                    break;
                }
            }
            return allowed;

        }).map(method -> {
            method.setAccessible(true);
            Triple<Command, Permissions[], String[]> information = getCommandInformation(method);
            return new SingleMethodRegistration(information.getThird()[0], information.getThird(), information.getFirst(), method, information.getSecond());
        }).toArray(SingleMethodRegistration[]::new);
    }

    /**
     Attempt to process the given command for the CommandSender.
     If no appropriate command is present false will be returned,
     in any other case true is returned.

     @param cmd
     The command to execute, without a preceding slash
     @param sender
     The command sender, usually a player, console or commandblock
     @param loc
     The location of the sender, if this is a non-player sender the location should be empty

     @return whether or not the command was processed by the command bus
     */
    public boolean process(String cmd, CommandSender sender, DarwinLocation loc) {
        if (sender == null) return false;
        Tuple<ParseResult, CommandContext> parseRes = parseContext(cmd, sender, loc);
        if (parseRes.getFirst() == null) return false;
        if (parseRes.getFirst().isSuccess()) {
            Triple<ParseResult, CommandContext, Method> succeededRes = (Triple<ParseResult, CommandContext, Method>) parseRes;
            Method method = succeededRes.getThird();
            List<Object> args = new ArrayList<>();
            for (Class<?> param : method.getParameterTypes()) {
                if (param.equals(CommandSender.class) || param.isAssignableFrom(CommandSender.class)) {
                    if (sender.getClass().equals(param) || sender.getClass().isAssignableFrom(param) || param.isAssignableFrom(sender.getClass())) {
                        args.add(sender);
                    } else {
                        args.add(null);
                    }
                } else if (param.equals(CommandContext.class)) {
                    args.add(succeededRes.getSecond());
                } else if (param.getAnnotation(Source.class) != null) {
                    if (param.equals(DarwinLocation.class)) {
                        args.add(loc);
                    } else if (param.equals(DarwinWorld.class)) {
                        args.add(loc.getWorld());
                    }
                } else {
                    args.add(null);
                }
            }
            String result = invoke(method, args.toArray());
            if (result != null) {
                sender.explainCommand("Something went wrong while trying to execute the command", null);
            }
        } else {
            if (parseRes instanceof Triple)
                sender.explainCommand(parseRes.getFirst().getMessage(), ((Triple<ParseResult, CommandContext, Command>) parseRes).getThird());
            else
                sender.explainCommand(parseRes.getFirst().getMessage(), null);
        }
        return true;
    }

    private String invoke(Method method, Object[] args) {
        try {
            Class<?> c = method.getDeclaringClass();
            Object o;
            if (c.equals(DarwinServer.class) || c.isAssignableFrom(DarwinServer.class) || DarwinServer.class.isAssignableFrom(c)) {
                o = DarwinServer.getServer();
            } else if (c.isAssignableFrom(Singleton.class)) {
                Field field = c.getDeclaredField("instance");
                o = field.get(null);
            } else {
                o = c.getConstructor().newInstance();
            }
            method.invoke(o, args);
            return null; // No error message to return
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | NoSuchFieldException e) {
            DarwinServer.error("Failed to invoke command", e);
            return e.getMessage();
        }
    }

    private Tuple<ParseResult, CommandContext> parseContext(String cmd, CommandSender sender, DarwinLocation loc) {
        String[] unparsedCommand = cmd.split(" ");
        if (unparsedCommand.length == 0) return new Tuple<>(new ParseResult("Command length is zero", false), null);

        String alias = unparsedCommand[0];
        CommandRegistration registration = COMMANDS.get(alias);
        if (registration == null) return new Tuple<>(null, null);

        if (registration instanceof ClassRegistration) {
            for (SingleMethodRegistration subcommand : ((ClassRegistration) registration).getSubcommands()) {
                boolean done = false;
                for (String subAlias : subcommand.getAliases()) {
                    if (cmd.startsWith(alias + " " + subAlias)) {
                        registration = subcommand;
                        done = true;
                        break;
                    }
                }
                if (done) break;
            }
            // After iterating subcommands the registration should be of type SingleMethodRegistration
            if (registration instanceof ClassRegistration)
                return new Tuple<>(new ParseResult("Missing subcommand, this is usually caused by a faulty plugin", false), null);
        }

        Command command = registration.getCommand();
        System.out.println("Registration for : " + cmd + " : " + registration);
        Permissions[] permissions = registration.getPermissions();
        for (Permissions permission : permissions) {
            DarwinServer.getLog().info("Has permission (" + permission.p() + ") : " + sender.hasPermission(permission));
            if (!sender.hasPermission(permission))
                return new Triple<>(new ParseResult(Translations.COMMAND_NO_PERMISSION.f(permission.p()), false), null, command);
        }

        // Wrapped by ArrayList as Arrays.asList is readonly, causing .remove(0) to throw UnsupportedOperationException
        List<String> unparsedArgs = new ArrayList<>(Arrays.asList(unparsedCommand));
        unparsedArgs.remove(0); // Command

        List<String> singularFlags = Arrays.asList(command.flags());
        List<String> valueFlags = Arrays.asList(command.valueFlags());
        List<CommandFlag<?>> flags = new ArrayList<>();
        List<CommandArgument<?>> arguments = new ArrayList<>();

        for (int i = 0; i < unparsedArgs.size(); i++) {
            String unparsedArg = unparsedArgs.get(i);

            // Flag
            if (unparsedArg.startsWith("-")) {
                String key = unparsedArg.replaceFirst("-", "");
                if (command.anyFlags() || singularFlags.contains(key)) {
                    CommandFlag<?> flag = CommandFlag.valueOf(key, null);
                    flags.add(flag);
                } else if (valueFlags.contains(key)) {
                    String value = unparsedArgs.get(i + 1);
                    unparsedArgs.remove(i+1);
                    CommandFlag<?> flag = CommandFlag.valueOf(key, value);
                    flags.add(flag);

                } else
                    return new Triple<>(new ParseResult(String.format("Unknown flag '%s'", key), false), null, command);
            } else {
                CommandArgument<?> argument;
                boolean joined = false;
                String currentKey = null;
                if (command.args().length > 0
                        && !command.args()[0].equals("")
                        && command.args().length >= arguments.size() + 1
                        && command.max() == command.args().length) {
                    currentKey = command.args()[arguments.size()];
                }
                if (command.join() && command.max() > -1 && i == command.max() - 1) {
                    String value = String.join(" ", unparsedArgs.subList(i, unparsedArgs.size() - 1));
                    argument = CommandArgument.valueOf(value, true, currentKey);
                    joined = true;
                } else {
                    argument = CommandArgument.valueOf(unparsedArg, false, currentKey);
                }
                arguments.add(argument);
                if (joined) break;
            }
        }

        if (command.max() != -1 && arguments.size() > command.max() && !command.join())
            return new Triple<>(new ParseResult("Too many arguments", false), null, command);

        if (arguments.size() < command.min())
            return new Triple<>(new ParseResult("Too few arguments", false), null, command);

        CommandContext ctx = new CommandContext(arguments.toArray(new CommandArgument[0]), sender, loc.getWorld(), loc, permissions, flags.toArray(new CommandFlag[0]));
        ParseResult result = new ParseResult("Succeeded", true);
        Method method = ((SingleMethodRegistration) registration).getMethod();
        return new Triple<>(result, ctx, method);
    }

}
