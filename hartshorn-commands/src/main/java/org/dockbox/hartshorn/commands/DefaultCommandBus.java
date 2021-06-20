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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.AbstractIdentifiable;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.api.entity.annotations.Entity;
import org.dockbox.hartshorn.api.events.parents.Cancellable;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.entry.DefaultResources;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.api.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandParameter;
import org.dockbox.hartshorn.commands.context.SimpleCommandContext;
import org.dockbox.hartshorn.commands.events.CommandEvent;
import org.dockbox.hartshorn.commands.registration.AbstractCommandContext;
import org.dockbox.hartshorn.commands.registration.MethodCommandContext;
import org.dockbox.hartshorn.commands.registration.ParentCommandContext;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.commands.values.AbstractArgumentElement;
import org.dockbox.hartshorn.commands.values.AbstractFlagCollection;
import org.dockbox.hartshorn.commands.values.ArgumentValue;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Entity(value = "commands", serializable = false)
@SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
public abstract class DefaultCommandBus<E> implements CommandBus {

    @Wired
    protected CommandResources resources;

    @Wired
    protected ApplicationContext context;


    private static final Map<String, ConfirmableQueueItem> confirmQueue = HartshornUtils.emptyConcurrentMap();

    private static final Map<String, AbstractCommandContext> registrations = HartshornUtils.emptyConcurrentMap();
    private static final Map<String, List<ParentCommandContext>> queuedAliases = HartshornUtils.emptyConcurrentMap();

    protected void callCommandContext(
            AbstractCommandContext registrationContext,
            String command,
            CommandSource sender,
            CommandContext ctx
    ) {
        /*
        If the command sender can be identified we can queue the command for confirmation. If the sender cannot be
        identified we cannot ensure the same command source is the one confirming the command, so we execute it as
        usual. The only sender with a bypass on this rule is the console.
        */
        //noinspection ConstantConditions
        if (registrationContext.getCommand().confirm() && sender instanceof AbstractIdentifiable && !(sender instanceof CommandInterface)) {
            String registrationId = AbstractCommandContext.getRegistrationId((Identifiable) sender, ctx);
            ConfirmableQueueItem queueItem = new ConfirmableQueueItem((AbstractIdentifiable) sender, ctx, registrationContext);
            DefaultCommandBus.queueConfirmable(registrationId, queueItem);

            Text confirmText = this.resources.getConfirmCommand().asText();
            confirmText.onHover(HoverAction.showText(this.resources.getConfirmCommandHover().asText()));
            confirmText.onClick(ClickAction.executeCallback(target -> this.context.get(CommandBus.class).confirmCommand(registrationId)));
            sender.sendWithPrefix(confirmText);

        }
        else {
            Exceptional<ResourceEntry> response = this.callCommandWithEvents(sender, ctx, command, registrationContext);

            if (response.caught())
                sender.sendWithPrefix(DefaultResources.instance().getUnknownError(response.error().getMessage()));
        }
    }

    protected static void queueConfirmable(String identifier, ConfirmableQueueItem queueItem) {
        DefaultCommandBus.confirmQueue.put(identifier, queueItem);
    }

    private Exceptional<ResourceEntry> callCommandWithEvents(
            CommandSource sender,
            CommandContext context,
            String command,
            AbstractCommandContext registrationContext
    ) {
        /*
        Modules are allowed to modify and/or cancel commands before and after the command has initially been
        executed. To allow this we need to ensure separate events are posted at these stages.
        */
        Cancellable ceb = new CommandEvent.Before(sender, context).post();

        if (!ceb.isCancelled()) {
            Exceptional<ResourceEntry> response = registrationContext.call(sender, context);
            new CommandEvent.After(sender, context).post();
            return response;
        }
        return Exceptional.empty();
    }

    @Override
    public void register(Class<?>... types) {
        for (Class<?> type : types) {
            if (null == type) continue;
            List<AbstractCommandContext> contexts = this.createContexts(type);

            for (AbstractCommandContext context : contexts) {
                for (String alias : context.getAliases()) {

                    if (context instanceof ParentCommandContext
                            && this.prepareInheritanceContext(context, alias)) continue;

                    if (DefaultCommandBus.getRegistrations().containsKey(alias))
                        Hartshorn.log().warn("Registering duplicate alias '" + alias + "'");

                    DefaultCommandBus.getRegistrations().put(alias, context);
                }
            }
        }
    }

    private List<AbstractCommandContext> createContexts(Class<?> parent) {
        List<AbstractCommandContext> contexts = HartshornUtils.emptyList();

        /*
        It is possible the class itself is not decorated with @Command, in which case all methods should be registered
        as individual commands. If the class itself is decorated with @Command, only methods marked as non-inherited
        should be registered as individual commands, and using all other methods decorated with @Command as
        sub-commands of the command defined in the class annotation.
        */
        boolean isParentRegistration = parent.isAnnotationPresent(Command.class);
        if (isParentRegistration)
            contexts.add(this.extractCommandInheritanceContext(parent));

        @NotNull
        @Unmodifiable
        Collection<Method> nonInheritedMethods = Reflect.annotatedMethods(parent, Command.class, c -> c.parent().equals(Void.class) && (!c.inherit() || !isParentRegistration));

        nonInheritedMethods.forEach(method -> {
            MethodCommandContext context = this.extractNonInheritedContext(method);
            if (null != context) contexts.add(context);
        });

        return contexts;
    }

    @SuppressWarnings("checkstyle:Indentation")
    private boolean prepareInheritanceContext(AbstractCommandContext context, String alias) {
        /*
        If the command 'extends' on the provided alias(es), it should not be registered directly and should instead be
        added to existing registrations. If no existing registration is present (yet), it should be queued so that it
        can be added when the target registration is created.
        */
        if (context.getCommand().extend()) {
            if (DefaultCommandBus.getRegistrations().containsKey(alias))
                this.addExtendingAliasToRegistration(
                        alias, (ParentCommandContext) context);
            else this.queueAliasRegistration(alias, (ParentCommandContext) context);

            return true;
        }
        else {
            /*
            If the command does not extend on the provided alias(es), it can be registered directly. These
            registrations can still be added to if future registrations extend aliases defined in this registration.
            However, once #apply is called these registrations are no longer mutable.
            */
            DefaultCommandBus.queuedAliases
                    .getOrDefault(alias, HartshornUtils.emptyConcurrentList())
                    .forEach(extendingContext ->
                            this.addExtendingContextToRegistration(extendingContext, (ParentCommandContext) context)
                    );
        }
        return false;
    }

    private ParentCommandContext extractCommandInheritanceContext(Class<?> parent) {
        Command command = parent.getAnnotation(Command.class);
        ParentCommandContext context = new ParentCommandContext(command, this.resources.getMissingArguments(), parent);

        /*
        Inherited methods are only stored inside the CommandInheritanceContext and not in
        DefaultCommandBus#registrations. This ensures no conflicts exist when registering sub-commands with equal
        aliases for different parents.
        */
        @NotNull
        @Unmodifiable
        Collection<Method> inheritedMethods = Reflect.annotatedMethods(parent, Command.class, Command::inherit);
        inheritedMethods.forEach(method -> context.addInheritedCommand(
                this.extractInheritedContext(parent, method, context))
        );

        return context;
    }
    
    @Nullable
    private MethodCommandContext extractNonInheritedContext(Method method) {
        Command command = method.getAnnotation(Command.class);
        if (command.inherit() && method.getDeclaringClass().isAnnotationPresent(Command.class))
            return null;

        return new MethodCommandContext(command, method);
    }

    private void addExtendingAliasToRegistration(String alias, ParentCommandContext extendingContext) {
        AbstractCommandContext context = DefaultCommandBus.getRegistrations().get(alias);
        this.addExtendingContextToRegistration(extendingContext, (ParentCommandContext) context);
        DefaultCommandBus.getRegistrations().put(alias, context);
    }

    private void queueAliasRegistration(String alias, ParentCommandContext context) {
        List<ParentCommandContext> contexts = DefaultCommandBus.queuedAliases.getOrDefault(alias, HartshornUtils.emptyConcurrentList());
        contexts.add(context);
        DefaultCommandBus.queuedAliases.put(alias, contexts);
    }

    private void addExtendingContextToRegistration(ParentCommandContext extendingContext, ParentCommandContext context) {
        extendingContext.getInheritedCommands().forEach(context::addInheritedCommand);
    }

    @Nullable
    private AbstractCommandContext extractInheritedContext(Class<?> parent, Method method, ParentCommandContext parentContext) {
        Command command = method.getAnnotation(Command.class);

        final String pkg = parent.getPackage().getName();
        final Collection<Class<?>> commandsInPackage = Reflect.annotatedTypes(pkg, Command.class);

        if (!command.inherit()) return null;
        AbstractCommandContext context = null;

        for (String alias : command.value()) {
            if (parentContext.getInheritedCommands().stream().anyMatch(cmd -> cmd.getAliases().contains(alias))) {
                /*
                If a parent has two sub-commands with the same alias(es), a warning is thrown. Typically methods calling
                this method will overwrite any existing sub-commands with the generated context.
                */
                Hartshorn.log().warn("Context for '" + parentContext.getPrimaryAlias() + "' has duplicate inherited context for '" + alias + "'.");
                continue;
            }

            final List<ParentCommandContext> subs = commandsInPackage.stream().filter(type -> {
                final Command annotation = type.getAnnotation(Command.class);
                if (!parent.equals(annotation.parent())) return false;

                final String[] aliases = annotation.value();
                return HartshornUtils.containsEqual(aliases, alias);
            }).map(this::extractCommandInheritanceContext).collect(Collectors.toList());

            if (subs.isEmpty()) continue;
            if (context == null) context = new ParentCommandContext(command, this.resources.getMissingArguments(), parent, method);

            for (ParentCommandContext sub : subs) {
                ((ParentCommandContext) context).addInheritedCommand(sub);
            }

        }
        if (context == null) context = new MethodCommandContext(command, method);
        return context;
    }

    @Override
    public void apply() {
        /*
        Each context is separately registered based on its alias(es). While it is possible to use
        AbstractRegistrationContext#getAliases to register all aliases at once, some command implementations may wish
        to be aware of the used alias.
        */
        DefaultCommandBus.getRegistrations().forEach((alias, abstractCommand) -> {
            E spec = null;
            if (abstractCommand instanceof ParentCommandContext) {
                ParentCommandContext inheritanceContext = (ParentCommandContext) abstractCommand;
                spec = this.buildInheritedContextExecutor(inheritanceContext, alias);
            }
            else if (abstractCommand instanceof MethodCommandContext) {
                MethodCommandContext methodContext = (MethodCommandContext) abstractCommand;
                if (!methodContext.getCommand().inherit()
                        || !methodContext.getDeclaringClass().isAnnotationPresent(Command.class)) {
                    spec = this.buildContextExecutor(methodContext, alias);
                }
                else {
                    Hartshorn.log().error("Found direct method registration of inherited command! " + methodContext.getLocation());
                }

            }
            else {
                Hartshorn.log().error("Found unknown context type [" + abstractCommand.getClass().getCanonicalName() + "]");
            }

            if (null != spec) {
                this.registerExecutor(spec, alias);
                Hartshorn.log().info("Registered /" + alias);
            }
            else
                Hartshorn.log().warn("Could not generate executor for '" + alias + "'. Any errors logged above.");
        });
        clearAliasQueue();
    }

    protected static Map<String, AbstractCommandContext> getRegistrations() {
        return DefaultCommandBus.registrations;
    }

    protected abstract E buildContextExecutor(AbstractCommandContext context, String alias);

    protected abstract E buildInheritedContextExecutor(ParentCommandContext context, String alias);

    protected abstract void registerExecutor(E executor, String alias);

    protected static void clearAliasQueue() {
        // This should be called after #apply has finished, to avoid unexpected alias extensions.
        DefaultCommandBus.queuedAliases.clear();
    }

    public Exceptional<Boolean> confirmCommand(String confirmId) {
        if (DefaultCommandBus.confirmQueue.containsKey(confirmId)) {
            ConfirmableQueueItem confirmableQueueItem = DefaultCommandBus.confirmQueue.get(confirmId);

            if (confirmableQueueItem.getSource() instanceof CommandSource) {
                confirmableQueueItem.getCommand().call(
                        (CommandSource) confirmableQueueItem.getSource(),
                        confirmableQueueItem.getContext());
                return Exceptional.of(true);
            }
        }
        return Exceptional.empty();
    }

    protected List<AbstractArgumentElement<?>> parseArgumentElements(CharSequence argString, String defaultPermission) {
        List<AbstractArgumentElement<?>> elements = HartshornUtils.emptyList();
        AbstractFlagCollection<?> flagCollection = null;

//        Matcher genericArgumentMatcher = GENERIC_ARGUMENT.matcher(argString);
//        while (genericArgumentMatcher.find()) {
//
//            String part = genericArgumentMatcher.group();
//            Matcher argumentMatcher = ARGUMENT.matcher(part);
//            if (argumentMatcher.matches()) {
//                this.extractArguments(elements, argumentMatcher, defaultPermission);
//
//            }
//            else {
//                Matcher flagMatcher = FLAG.matcher(part);
//                flagCollection = this.getAbstractFlagCollection(flagCollection, flagMatcher, defaultPermission);
//            }
//        }

        /*
        Certain platforms may require the flag collection to be parsed together with the wrapped arguments. It is
        possible that a platform implementation returns a flat list of arguments and flags here, though to avoid
        incompatibilities the option to build and combine these is provided.
        */
        if (null == flagCollection) return elements;
        else return flagCollection.buildAndCombines(this.wrapElements(elements));
    }

    private AbstractFlagCollection<?> getAbstractFlagCollection(
            AbstractFlagCollection<?> flagCollection,
            Matcher flagMatcher,
            String defaultPermission
    ) {
        if (flagMatcher.matches()) {
            if (null == flagCollection) flagCollection = this.context.get(AbstractFlagCollection.class);
            this.parseFlag(flagCollection, flagMatcher.group(1), flagMatcher.group(2), defaultPermission);
        }
        return flagCollection;
    }

    private void parseFlag(AbstractFlagCollection<?> flags, String name, String value, String defaultPermission) {
        if (null == value) {
            int at;
            /* See syntax definition of DefaultCommandBus#FLAG */
            if (0 <= (at = name.indexOf(':'))) {
                name = name.substring(0, at);
                String permission = name.substring(at + 1);
                flags.addNamedPermissionFlag(name, permission);
            }
            else {
                flags.addNamedFlag(name);
            }

        }
        else {
            ArgumentValue<?> argumentValue = this.generateArgumentValue(value, defaultPermission);
            if (0 <= name.indexOf(':')) {
                Except.handle("Flag values do not support permissions at flag `" + name + "`. Permit the value instead");
            }
            flags.addValueBasedFlag(name, argumentValue);
        }
    }

    protected SimpleCommandContext createCommandContext(String command, CommandSource sender, Map<String, Collection<Object>> args) {
        List<CommandParameter<?>> arguments = HartshornUtils.emptyList();
        List<CommandParameter<?>> flags = HartshornUtils.emptyList();

        assert null != command : "Context carrier command was null";
        args.forEach((key, parsedArguments) ->
                parsedArguments.forEach(obj -> {
                    /*
                    Simple pattern check to see if a parsed element is a flag. As these elements are already parsed the pattern
                    does not have to check for anything but the flag prefix (-f or --flag).
                    */
                    if (Pattern.compile("-(-?" + key + ")").matcher(command).find())
                        flags.add(new CommandParameter<>(this.tryConvertObject(obj), key));
                    else
                        arguments.add(new CommandParameter<>(this.tryConvertObject(obj), key));
                }));

        return new SimpleCommandContext(
                command,
                arguments.toArray(new CommandParameter<?>[0]),
                flags.toArray(new CommandParameter<?>[0]),
                sender,
                new String[0]);
    }

    protected abstract Object tryConvertObject(Object obj);

    protected abstract ArgumentValue<?> getArgumentValue(String type, String permission, String key);

    protected abstract AbstractArgumentElement<?> wrapElements(List<AbstractArgumentElement<?>> elements);

}
