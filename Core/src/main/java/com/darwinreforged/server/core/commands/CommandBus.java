package com.darwinreforged.server.core.commands;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.commands.annotations.Permission;
import com.darwinreforged.server.core.commands.annotations.Source;
import com.darwinreforged.server.core.commands.context.CommandArgument;
import com.darwinreforged.server.core.commands.context.CommandContext;
import com.darwinreforged.server.core.commands.context.CommandFlag;
import com.darwinreforged.server.core.commands.registrations.ClassRegistration;
import com.darwinreforged.server.core.commands.registrations.CommandRegistration;
import com.darwinreforged.server.core.commands.registrations.SingleMethodRegistration;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.tuple.QuadTuple;
import com.darwinreforged.server.core.tuple.Triple;
import com.darwinreforged.server.core.tuple.Tuple;
import com.darwinreforged.server.core.types.internal.Singleton;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.util.CommandUtils;

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
import java.util.Optional;
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

    public void register(Object... objs) {
        for (Object obj : objs) {
            Class<?> clazz;
            if (obj instanceof Class) clazz = (Class<?>) obj;
            else clazz = obj.getClass();

            DarwinServer.getLog().info(String.format("\n\nScanning %s for commands", clazz.toGenericString()));
            try {
                if (clazz.isAnnotationPresent(Command.class)) {
                    ClassRegistration registration = handleClassType(clazz);
                    Arrays.stream(registration.getAliases()).forEach(alias -> {
                        if (!(obj instanceof Class)) registration.setSourceInstance(obj);
                        Arrays.stream(registration.getSubcommands()).forEach(subRegistration -> {
                            registerCommand(subRegistration.getCommand().context(), subRegistration.getPermissions()[0].p(), (s, c) -> {
                                // TODO : redirect to single method invoke
                            });
                        });
                        registerCommand('*' + registration.getCommand().context(), registration.getPermissions()[0].p(), (s, c) -> {});
                        String[] subcommands = Arrays.stream(registration.getSubcommands()).map(scmd -> scmd.getAliases()[0]).toArray(String[]::new);
                        DarwinServer.getLog().info(String.format("Registered command : /%s %s", alias, String.join("|", subcommands)));
                    });
                } else {
                    List<Method> methods = new ArrayList<>();
                    for (Method method : clazz.getDeclaredMethods()) {
                        method.setAccessible(true);
                        if (method.isAnnotationPresent(Command.class)) methods.add(method);
                    }
                    SingleMethodRegistration[] registrations = handleSingleMethod(methods);
                    Arrays.stream(registrations)
                            .forEach(registration -> Arrays.stream(registration.getAliases())
                                    .forEach(alias -> {
                                        registerCommand(registration.getCommand().context(), registration.getPermissions()[0].p(), (s, c) -> {
                                            // TODO : redirect to direct method invoke
                                        });
                                        DarwinServer.getLog().info("Registered singular command : /" + alias);
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
            QuadTuple<ParseResult, CommandContext, Method, CommandRegistration> succeededRes = (QuadTuple<ParseResult, CommandContext, Method, CommandRegistration>) parseRes;
            Method method = succeededRes.getThird();
            List<Object> args = new ArrayList<>();
            for (Class<?> param : method.getParameterTypes()) {
                if (param.equals(CommandSender.class) || param.isAssignableFrom(CommandSender.class) || CommandSender.class.isAssignableFrom(param)) {
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
            String result = invoke(method, args.toArray(), succeededRes.getFourth());
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

    private String invoke(Method method, Object[] args, CommandRegistration registration) {
        try {
            Class<?> c = method.getDeclaringClass();
            Object o;
            if (registration.getSourceInstance().isPresent()) {
                o = registration.getSourceInstance().get();
            } else if (c.equals(DarwinServer.class) || c.isAssignableFrom(DarwinServer.class) || DarwinServer.class.isAssignableFrom(c)) {
                o = DarwinServer.getServer();
            } else if (c.isAssignableFrom(Singleton.class)) {
                Field field = c.getDeclaredField("instance");
                o = field.get(null);
            } else {
                Optional<?> modOptional;
                if (c.isAnnotationPresent(Module.class) && (modOptional = DarwinServer.getModule(c)).isPresent()) {
                    o = modOptional.get();
                } else {
                    o = c.getConstructor().newInstance();
                }
            }
            method.invoke(o, args);
            return null; // No error message to return
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | NoSuchFieldException e) {
            DarwinServer.error("Failed to invoke command", e);
            return e.getMessage();
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    private Tuple<CommandRegistration, String[]> getRegistrationAndArguments(String command, CommandSender sender) {
        String alias = command.split(" ")[0];
        CommandRegistration registration = COMMANDS.get(alias);
        if (registration == null) return new Tuple<>(null, new String[]{"Could not find command"});

        String[] arguments = new String[0];
        if (registration instanceof ClassRegistration) {
            for (SingleMethodRegistration subcommand : ((ClassRegistration) registration).getSubcommands()) {
                boolean done = false;
                for (String subAlias : subcommand.getAliases()) {
                    String fullCommand = alias + " " + subAlias;
                    if (subAlias.equals("")) {
                        arguments = command.replaceFirst(alias, "").split(" ");
                        registration = subcommand;
                        // Continue to iterate in case there are still aliases left to check, if one was already found
                        // the loop would already be broken, so we never overwrite values here
                    } else if (command.startsWith(fullCommand)) {
                        registration = subcommand;
                        if (command.equals(fullCommand)) arguments = new String[0];
                        else
                            arguments = command.replaceFirst(String.format("%s %s ", alias, subAlias), "").split(" ");

                        done = true;
                        break;
                    }
                }
                if (done) break;
            }
            // After iterating subcommands the registration should be of type SingleMethodRegistration
            if (registration instanceof ClassRegistration) {
                String[] subcommands = Arrays.stream(((ClassRegistration) registration).getSubcommands())
                        .filter(sub -> {
                            boolean permitted = true;
                            for (Permissions permission : sub.getPermissions()) {
                                if (!sender.hasPermission(permission)) {
                                    permitted = false;
                                    break;
                                }
                            }
                            return permitted;
                        })
                        .map(sub -> sub.getAliases()[0] + " : " + sub.getCommand().desc()).toArray(String[]::new);
                return new Tuple<>(null,
                        new String[]{String.format("Incorrect usage for %s, available sub-commands : \n%s", registration.getAliases()[0], String.join("\n", subcommands))});
            }
        }
        return new Tuple<>(registration, arguments);
    }

    private Tuple<List<CommandArgument<?>>, List<CommandFlag<?>>> getArgsAndFlags(Command command, List<String> unparsedArguments) {
        List<String> singularFlags = Arrays.asList(command.flags());
        List<String> valueFlags = Arrays.asList(command.valueFlags());

        List<CommandArgument<?>> arguments = new ArrayList<>();
        List<CommandFlag<?>> flags = new ArrayList<>();

        for (int i = 0; i < unparsedArguments.size(); i++) {
            String unparsedArg = unparsedArguments.get(i);

            if (unparsedArg.startsWith("-")) {
                String key = unparsedArg.replaceFirst("-", "");
                if (command.anyFlags() || singularFlags.contains(key)) {
                    CommandFlag<?> flag = CommandFlag.valueOf(key, null);
                    flags.add(flag);

                } else if (valueFlags.contains(key)) {
                    String value = unparsedArguments.get(i + 1);
                    unparsedArguments.remove(i+1);
                    CommandFlag<?> flag = CommandFlag.valueOf(key, value);
                    flags.add(flag);

                } else return null;

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
                    String value = String.join(" ", unparsedArguments.subList(i, unparsedArguments.size() - 1));
                    argument = CommandArgument.valueOf(value, true, currentKey);
                    joined = true;

                } else {
                    argument = CommandArgument.valueOf(unparsedArg, false, currentKey);
                }

                arguments.add(argument);
                if (joined) break;
            }
        }
        return new Tuple<>(arguments, flags);
    }

    private void registerCommand(String command, String permission, CommandRunner runner) {
        CommandUtils<?, ?> utils = DarwinServer.getUtilChecked(CommandUtils.class);
        if (command.indexOf(' ')<0 && !command.startsWith("*")) utils.registerCommandNoArgs(command, permission, runner);
        else utils.registerCommandArgsAndOrChild(command, permission, runner);
    }

    private Tuple<ParseResult, CommandContext> parseContext(String cmd, CommandSender sender, DarwinLocation loc) {
        String[] unparsedCommand = cmd.split(" ");
        if (unparsedCommand.length == 0) return new Tuple<>(new ParseResult("Command length is zero", false), null);

        Tuple<CommandRegistration, String[]> registrationAndArgs = getRegistrationAndArguments(cmd, sender);

        CommandRegistration registration = registrationAndArgs.getFirst();
        if (registration == null || registration instanceof ClassRegistration) {
            String error = registrationAndArgs.getSecond()[0];
            return new Tuple<>(new ParseResult(error, false), null);
        }

        Command command = registration.getCommand();
        Permissions[] permissions = registration.getPermissions();
        for (Permissions permission : permissions) {
            if (!sender.hasPermission(permission))
                return new Triple<>(new ParseResult(Translations.COMMAND_NO_PERMISSION.f(permission.p()), false), null, command);
        }

        // Wrapped by ArrayList as Arrays.asList is readonly, causing .remove(0) to throw UnsupportedOperationException
        Tuple<List<CommandArgument<?>>, List<CommandFlag<?>>> argumentsAndFlags = getArgsAndFlags(command, new ArrayList<>(Arrays.asList(unparsedCommand)));
        List<CommandArgument<?>> arguments = argumentsAndFlags.getFirst();
        List<CommandFlag<?>> flags = argumentsAndFlags.getSecond();

        if (command.max() != -1 && arguments.size() > command.max() && !command.join())
            return new Triple<>(new ParseResult("Too many arguments", false), null, command);

        if (arguments.size() < command.min())
            return new Triple<>(new ParseResult("Too few arguments", false), null, command);

        CommandContext ctx = new CommandContext(arguments.toArray(new CommandArgument[0]), sender, loc.getWorld(), loc, permissions, flags.toArray(new CommandFlag[0]));
        ParseResult result = new ParseResult("Succeeded", true);
        Method method = ((SingleMethodRegistration) registration).getMethod();
        return new QuadTuple<>(result, ctx, method, registration);
    }

    @FunctionalInterface
    public interface CommandRunner {
        void run(CommandSender sender, CommandContext ctx);
    }
}
