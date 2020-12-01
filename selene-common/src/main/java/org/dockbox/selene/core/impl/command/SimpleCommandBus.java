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

package org.dockbox.selene.core.impl.command;

import org.dockbox.selene.core.annotations.command.Command;
import org.dockbox.selene.core.annotations.command.FromSource;
import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.command.CommandRunnerFunction;
import org.dockbox.selene.core.command.context.CommandContext;
import org.dockbox.selene.core.command.registry.AbstractCommandRegistration;
import org.dockbox.selene.core.command.registry.ClassCommandRegistration;
import org.dockbox.selene.core.command.registry.MethodCommandRegistration;
import org.dockbox.selene.core.exceptions.ConfirmFailedException;
import org.dockbox.selene.core.exceptions.IllegalSourceException;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.i18n.permissions.AbstractPermission;
import org.dockbox.selene.core.i18n.permissions.ExternalPermission;
import org.dockbox.selene.core.i18n.permissions.Permission;
import org.dockbox.selene.core.impl.command.context.SimpleCommandContext;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.targets.CommandSource;
import org.dockbox.selene.core.objects.targets.Console;
import org.dockbox.selene.core.objects.targets.Identifiable;
import org.dockbox.selene.core.objects.tuple.Triad;
import org.dockbox.selene.core.objects.user.Player;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.actions.ClickAction;
import org.dockbox.selene.core.text.actions.HoverAction;
import org.dockbox.selene.core.SeleneUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SimpleCommandBus<C, A extends AbstractArgumentValue<?>> implements CommandBus {

    private final Map<UUID, Runnable> confirmableCommands = SeleneUtils.emptyConcurrentMap();

    private static final List<String> RegisteredCommands = SeleneUtils.emptyConcurrentList();
    private static final Pattern argFinder = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))"); //each match is a flag or argument
    private static final Pattern flag = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?"); //g1: name  (g2: value)
    private static final Pattern argument = Pattern.compile("([\\[<])(.+)[\\]>]"); //g1: <[  g2: run argFinder, if nothing it's a value
    private static final Pattern value = Pattern.compile("(\\w+)(?:\\{(\\w+)(?::([\\w\\.]+))?\\})?"); //g1: name  g2: if present type, other wise use g1
    private static final Pattern subcommand = Pattern.compile("[a-z]*");
    private static final String parentCommandPrefix = "$:";

    @Override
    public void register(@NotNull Object @NotNull ... objs) {
        for (Object obj : objs) {
            Class<?> clazz = obj instanceof Class<?> ? (Class<?>) obj : obj.getClass();
            Selene.log().info("Scanning {} for commands", clazz.toGenericString());
            try {
                if (clazz.isAnnotationPresent(Command.class)) {
                    this.registerClassCommand(clazz, obj);
                }
                this.registerSingleMethodCommands(clazz);
            } catch (Throwable e) {
                Selene.getServer().except("Failed to register potential command class : [" + clazz.getCanonicalName() + "]", e);
            }
        }
    }

    @Override
    public void registerSingleMethodCommands(@NotNull Class<?> clazz) {
        Collection<Method> methods = SeleneUtils.getAnnotedMethods(clazz, Command.class, c -> !c.inherit());
        MethodCommandRegistration[] registrations = this.createSingleMethodRegistrations(methods);
        Arrays.stream(registrations).forEach(registration -> Arrays.stream(registration.getAliases())
                .forEach(alias -> this.registerSingleMethodRegistration(registration, alias)));
    }

    private void registerSingleMethodRegistration(MethodCommandRegistration registration, String alias) {
        String usage = registration.getCommand().usage();
        String next = usage.contains(" ")
                ? usage.replaceFirst(usage.substring(0, usage.indexOf(' ')), alias)
                : usage;
        this.registerCommand(next, registration.getPermissions(), (src, ctx) ->
                this.processRunnableCommand(registration, src, ctx));
        Selene.log().info("Registered singular command : {}", alias);
    }

    private void processRunnableCommand(MethodCommandRegistration registration, CommandSource src, CommandContext ctx) {
        Runnable runnable = () -> {
            Exceptional<IntegratedResource> result = this.invoke(registration.getMethod(), src, ctx, registration);
            if (result.errorPresent())
                src.sendWithPrefix(IntegratedResource.UNKNOWN_ERROR.format(result.getError().getMessage()));
            else if (result.isPresent()) src.sendWithPrefix(result.get());
        };

        if (registration.getCommand().confirm() && src instanceof Identifiable) {
            this.confirmableCommands.put(((Identifiable<?>) src).getUniqueId(), runnable);

            Text confirmMessage = Text.of(IntegratedResource.CONFIRM_COMMAND_MESSAGE)
                    .onClick(new ClickAction.RunCommand("/selene confirm " + ((Identifiable<?>) src).getUniqueId()))
                    .onHover(new HoverAction.ShowText(Text.of(IntegratedResource.CONFIRM_COMMAND_MESSAGE_HOVER)));

            src.sendWithPrefix(confirmMessage);

            /* If the source cannot be identified we cannot ensure the
               confirmer is the same source as the original executor */
        } else runnable.run();
    }

    @Override
    public void registerClassCommand(@NotNull Class<?> clazz, @NotNull Object instance) {
        ClassCommandRegistration registration = this.createClassRegistration(clazz);
        Arrays.stream(registration.getAliases()).forEach(alias -> {
            if (instance instanceof Class) registration.setSourceInstance(instance);

            AtomicReference<CommandRunnerFunction> parentRunner = new AtomicReference<>((src, ctx) -> {
                src.sendWithPrefix("This command requires arguments!");// TODO, ResourceEntry
            });

            Arrays.stream(registration.getSubcommands())
                    .forEach(subRegistration -> this.registerMethodRegistration(subRegistration, alias, parentRunner));

            String usage = registration.getCommand().usage();
            String next = usage.contains(" ") ? usage.replaceFirst(usage.substring(0, usage.indexOf(' ')), alias) : alias;
            this.registerCommand('*' + next, registration.getPermissions(), parentRunner.get());

            List<String> subcommands = SeleneUtils.emptyList();
            Arrays.stream(registration.getSubcommands()).forEach(sub -> subcommands.addAll(SeleneUtils.asList(sub.getAliases())));
            Selene.log().info("Registered command : /{} {}", alias, String.join("|", subcommands));
        });
    }

    private void registerMethodRegistration(MethodCommandRegistration registration, String alias, AtomicReference<CommandRunnerFunction> parentRunner) {
        CommandRunnerFunction methodRunner = (src, ctx) -> this.processRunnableCommand(registration, src, ctx);
        Arrays.stream(registration.getAliases()).forEach((@NonNls String rAlias) -> {
            if ("".equals(rAlias)) {
                parentRunner.set(methodRunner);
            } else {
                // Sub commands need the parent command in the context so it can register correctly
                String usage = rAlias + ' ' + registration.getCommand().usage();
                String next = usage.contains(" ") ? usage.replace(usage.substring(0, usage.indexOf(' ')), alias) : usage;
                this.registerCommand(next, registration.getPermissions(), methodRunner);
            }
        });
    }

    @NotNull
    @Override
    public ClassCommandRegistration createClassRegistration(@NotNull Class<?> clazz) {
        Triad<Command, AbstractPermission, String[]> information = this.getCommandInformation(clazz);
        Collection<Method> methods = SeleneUtils.getAnnotedMethods(clazz, Command.class, c -> !c.inherit());
        MethodCommandRegistration[] registrations = this.createSingleMethodRegistrations(methods);
        return new ClassCommandRegistration(
                information.getThird()[0],
                information.getThird(),
                information.getSecond(),
                information.getFirst(),
                clazz,
                registrations
        );
    }

    private Triad<Command, AbstractPermission, String[]> getCommandInformation(AnnotatedElement element) {
        Command command = element.getAnnotation(Command.class);
        @SuppressWarnings("CallToSuspiciousStringMethod")
        AbstractPermission permission = "".equals(command.rawPermission())
                ? command.permission()
                : new ExternalPermission(command.rawPermission());
        return new Triad<>(command, permission, command.aliases());
    }

    @NotNull
    @Override
    public MethodCommandRegistration[] createSingleMethodRegistrations(@NotNull Collection<Method> methods) {
        return methods.stream().filter(this::filterSingleMethodCandidate).map(method -> {
            method.setAccessible(true);
            Triad<Command, AbstractPermission, String[]> information = this.getCommandInformation(method);
            return new MethodCommandRegistration(information.getThird()[0], information.getThird(), information.getFirst(), method, information.getSecond());
        }).toArray(MethodCommandRegistration[]::new);
    }

    private boolean filterSingleMethodCandidate(Method method) {
        final List<Class<?>> commandTypes = SeleneUtils.asList(CommandSource.class, SimpleCommandContext.class);
        final List<Class<?>> locationTypes = SeleneUtils.asList(World.class, Location.class);

        boolean allowed = true;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> type : parameterTypes) {
            boolean defaultTypeAssignable = false;
            for (Class<?> ct : commandTypes) {
                if (SeleneUtils.isAssignableFrom(ct, type)) {
                    defaultTypeAssignable = true;
                    break;
                }
            }

            boolean locationTypeAssignable = false;
            for (Class<?> lt : locationTypes) {
                if (SeleneUtils.isAssignableFrom(lt, type)) {
                    locationTypeAssignable = true;
                    break;
                }
            }
            if (!(defaultTypeAssignable || type.isAnnotationPresent(FromSource.class) && locationTypeAssignable)) {
                allowed = false;
                break;
            }
        }
        return allowed;
    }

    @Override
    public void registerCommand(@NotNull String command, @NotNull AbstractPermission permission, @NotNull CommandRunnerFunction runner) {
        if (0 > command.indexOf(' ') && !command.startsWith(parentCommandPrefix))
            this.registerCommand(command, permission, runner);
        else this.registerCommandArgsAndOrChild(command, permission, runner);
    }

    protected void registerCommandArgsAndOrChild(String command, AbstractPermission permission, CommandRunnerFunction runner) {
        Selene.log().debug("Registering command '{}' with singluar permission ({})", command, permission.get());
        String[] parts = command.split(" ");
        String part = 1 < parts.length ? parts[1] : null;
        if (null != part && subcommand.matcher(part).matches()) {
            this.registerChildCommand(command, runner, part, permission);
        } else if (command.startsWith(parentCommandPrefix)) {
            this.registerParentCommand(command, runner, permission);
        } else {
            this.registerSingleMethodCommand(command, runner, part, permission);
        }
    }

    @NotNull
    @Override
    public Exceptional<Boolean> confirmLastCommand(@NotNull UUID uuid) {
        Map<UUID, Runnable> confirmableCommandSnapshot = this.confirmableCommands;
        if (confirmableCommandSnapshot.containsKey(uuid)) {
            Runnable runnable = confirmableCommandSnapshot.get(uuid);
            this.confirmableCommands.remove(uuid);
            if (null != runnable) {
                runnable.run();
                return Exceptional.of(true);
            } else {
                return Exceptional.of(false, new ConfirmFailedException(IntegratedResource.CONFIRM_INVALID_ENTRY.asString()));
            }
        } else {
            return Exceptional.of(false, new ConfirmFailedException(IntegratedResource.CONFIRM_EXPIRED.asString()));
        }
    }

    private Exceptional<IntegratedResource> invoke(Method method, CommandSource sender, CommandContext ctx, AbstractCommandRegistration registration) {
        if (this.isSenderInCooldown(sender, ctx, method)) {
            return Exceptional.of(IntegratedResource.IN_ACTIVE_COOLDOWN);
        }

        Class<?> declaringClass = method.getDeclaringClass();
        try {
            List<Object> args = this.prepareInvokeArguments(method, sender, ctx);
            Object instance = this.prepareInvokeInstance(registration, declaringClass);
            Command command = method.getAnnotation(Command.class);
            if (0 < command.cooldownDuration() && sender instanceof Identifiable) {
                String registrationId = this.getRegistrationId((Identifiable<?>) sender, ctx);
                SeleneUtils.cooldown(registrationId, command.cooldownDuration(), command.cooldownUnit());
            }
            method.invoke(instance, SeleneUtils.toArray(Object.class, args));
            return Exceptional.empty();
        } catch (IllegalSourceException e) {
            return Exceptional.of(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Selene.getServer().except("Failed to invoke command", e.getCause());
            return Exceptional.of(e);
        } catch (Throwable e) {
            Selene.getServer().except("Failed to invoke command", e);
            return Exceptional.of(e);
        }
    }

    private boolean isSenderInCooldown(CommandSource sender, CommandContext ctx, Method method) {
        Command command = method.getAnnotation(Command.class);
        if (0 >= command.cooldownDuration()) return false;
        if (sender instanceof Identifiable) {
            String registrationId = this.getRegistrationId((Identifiable<?>) sender, ctx);
            return SeleneUtils.isInCooldown(registrationId);
        }
        return false;
    }

    private String getRegistrationId(Identifiable<?> sender, CommandContext ctx) {
        UUID uuid = sender.getUniqueId();
        String alias = ctx.getAlias();
        return uuid + "$" + alias;
    }

    private Object prepareInvokeInstance(AbstractCommandRegistration registration, Class<?> declaringClass) {
        Object instance;
        if (null != registration.getSourceInstance() && !(registration.getSourceInstance() instanceof Method)) {
            instance = registration.getSourceInstance();
        } else if (declaringClass.equals(Selene.class) || SeleneUtils.isAssignableFrom(Selene.class, declaringClass)) {
            instance = Selene.getServer();
        } else {
            instance = Selene.getInstance(declaringClass);
        }
        return instance;
    }

    private List<Object> prepareInvokeArguments(Method method, CommandSource sender, CommandContext ctx) {
        List<Object> finalArgs = SeleneUtils.emptyList();

        for (Class<?> parameterType : method.getParameterTypes()) {
            if (SeleneUtils.isAssignableFrom(CommandSource.class, parameterType)) {
                if (parameterType.equals(Player.class)) {
                    if (sender instanceof Player) finalArgs.add(sender);
                    else throw new IllegalSourceException("Command can only be ran by players");
                } else if (parameterType.equals(Console.class)) {
                    if (sender instanceof Console) finalArgs.add(sender);
                    else throw new IllegalSourceException("Command can only be ran by the console");
                } else finalArgs.add(sender);
            } else if (SeleneUtils.isAssignableFrom(CommandContext.class, parameterType)) {
                finalArgs.add(ctx);
            } else {
                throw new IllegalStateException("Method requested parameter type '" + parameterType.toGenericString() + "' which is not provided");
            }
        }
        return finalArgs;
    }

    protected A getArgumentValue(String value) {
        String type;
        String key;
        String permission;
        Matcher vm = SimpleCommandBus.value.matcher(value);
        if (!vm.matches() || 0 == vm.groupCount())
            Selene.getServer().except("Unknown argument specification " + value + ", use Type or Name{Type} or Name{Type:Permission}");

        if (1 <= vm.groupCount()) key = vm.group(1);
        else throw new IllegalArgumentException("Missing key argument in specification '" + value + "'");
        if (2 <= vm.groupCount()) type = vm.group(2);
        else type = "String";
        if (3 <= vm.groupCount()) permission = vm.group(3);
        else permission = Permission.GLOBAL_BYPASS.get();

        return this.getArgumentValue(type, Permission.Companion.of(permission), key);
    }

    public Map<UUID, Runnable> getConfirmableCommands() {
        return this.confirmableCommands;
    }

    public static List<String> getRegisteredCommands() {
        return RegisteredCommands;
    }

    public static Pattern getArgFinder() {
        return argFinder;
    }

    public static Pattern getFlag() {
        return flag;
    }

    public static Pattern getArgument() {
        return argument;
    }

    public static Pattern getValue() {
        return value;
    }

    public static Pattern getSubcommand() {
        return subcommand;
    }

    public static String getParentCommandPrefix() {
        return parentCommandPrefix;
    }

    protected abstract A getArgumentValue(String type, AbstractPermission permission, String key);

    protected abstract SimpleCommandContext convertContext(C ctx, CommandSource sender, String command);

    protected abstract void registerCommandNoArgs(String command, AbstractPermission permission, CommandRunnerFunction runner);

    protected abstract void registerChildCommand(String command, CommandRunnerFunction runner, String usage, AbstractPermission permission);

    protected abstract void registerSingleMethodCommand(String command, CommandRunnerFunction runner, String usage, AbstractPermission permission);

    protected abstract void registerParentCommand(String command, CommandRunnerFunction runner, AbstractPermission permission);
}
