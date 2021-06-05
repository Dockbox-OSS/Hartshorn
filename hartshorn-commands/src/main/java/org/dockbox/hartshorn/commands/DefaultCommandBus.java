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
import org.dockbox.hartshorn.commands.registration.AbstractRegistrationContext;
import org.dockbox.hartshorn.commands.registration.CommandInheritanceContext;
import org.dockbox.hartshorn.commands.registration.MethodCommandContext;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.commands.values.AbstractArgumentElement;
import org.dockbox.hartshorn.commands.values.AbstractFlagCollection;
import org.dockbox.hartshorn.commands.values.ArgumentValue;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.util.Reflect;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(value = "commands", serializable = false)
@SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
public abstract class DefaultCommandBus<E> implements CommandBus {

    @Wired
    protected CommandResources resources;

    @Wired
    protected ApplicationContext context;

    /**
     * Represents the default type for command elements matched by {@link DefaultCommandBus#FLAG} or
     * {@link DefaultCommandBus#ARGUMENT}. If no type is defined in those matches, this value is used.
     * 'String' is used as this is the base value provided to Hartshorn, thus requiring no further
     * converting to other data types.
     */
    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final String DEFAULT_TYPE = "String";

    /**
     * Each matching element represents either a flag or argument, these can then be parsed using
     * {@link DefaultCommandBus#FLAG} and {@link DefaultCommandBus#ARGUMENT}.
     */
    private static final Pattern GENERIC_ARGUMENT = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))");

    /**
     * Each matching element represents a flag with either one or two groups. The first group (G1) is
     * required, and indicates the name of the flag. The second group (G2) is optional, and represents
     * the value expected by the flag. G2 is a argument which can be parsed using {@link
     * DefaultCommandBus#ARGUMENT}.
     *
     * <p>Syntax:
     *
     * <ul>
     *   <li>Without value: -f, --flag
     *   <li>With simple value: -f Type, --flag Type
     *   <li>With permission value: -f Type:Permission, --flag Type:Permission
     * </ul>
     */
    private static final Pattern FLAG = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?");

    /**
     * Each matching element represents a argument with two groups. The first group (G1) indicates
     * whether the argument is required or optional. The second group can either be a argument meta
     * which can be parsed using {@link DefaultCommandBus#ELEMENT_VALUE}, or a simple value if {@link
     * DefaultCommandBus#ELEMENT_VALUE} returns no matches. Arguments can be grouped.
     *
     * <p>Syntax:
     *
     * <ul>
     *   <li>Optional without type: [Argument]
     *   <li>Optional with simple value: [Argument{Type}]
     *   <li>Optional with permission value: [Argument{Type:Permission}]
     *   <li>Required without type: &lt;Argument&gt;
     *   <li>Required with value is equal in syntax to optional, but wrapped in &lt;&gt;
     *   <li>Argument group: [&lt;Argument&gt; &lt;Argument{Type}&gt;]
     * </ul>
     */
    private static final Pattern ARGUMENT = Pattern.compile("([\\[<])(.+)[\\]>]");

    /**
     * Each matching element represents additional meta information for matching elements of {@link
     * DefaultCommandBus#ARGUMENT}. Matches contain either one or two groups. If both groups are
     * present, group 1 represents the name of the argument, and group 2 represents the value. If only
     * group 1 is present, it represents the type of the argument and the name is obtained from the
     * argument definition.
     *
     * <p>Syntax:
     *
     * <ul>
     *   <li>Type
     *   <li>Name{Type}
     *   <li>Name{Type:Permission}
     * </ul>
     */
    private static final Pattern ELEMENT_VALUE = Pattern.compile("(\\w+)(?:\\{(\\w+)(?::([\\w\\.]+))?\\})?");

    private static final Map<String, ConfirmableQueueItem> confirmQueue = HartshornUtils.emptyConcurrentMap();

    private static final Map<String, AbstractRegistrationContext> registrations = HartshornUtils.emptyConcurrentMap();
    private static final Map<String, List<CommandInheritanceContext>> queuedAliases = HartshornUtils.emptyConcurrentMap();

    protected void callCommandContext(
            AbstractRegistrationContext registrationContext,
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
            String registrationId = AbstractRegistrationContext.getRegistrationId((Identifiable) sender, ctx);
            ConfirmableQueueItem queueItem = new ConfirmableQueueItem((AbstractIdentifiable) sender, ctx, registrationContext);
            DefaultCommandBus.queueConfirmable(registrationId, queueItem);

            Text confirmText = this.resources.getConfirmCommand().asText();
            confirmText.onHover(HoverAction.showText(this.resources.getConfirmCommandHover().asText()));
            confirmText.onClick(ClickAction.executeCallback(target -> this.context.get(CommandBus.class).confirmCommand(registrationId)));
            sender.sendWithPrefix(confirmText);

        }
        else {
            Exceptional<ResourceEntry> response = DefaultCommandBus.callCommandWithEvents(sender, ctx, command, registrationContext);

            if (response.caught())
                sender.sendWithPrefix(DefaultResources.instance().getUnknownError(response.error().getMessage()));
        }
    }

    protected static void queueConfirmable(String identifier, ConfirmableQueueItem queueItem) {
        DefaultCommandBus.confirmQueue.put(identifier, queueItem);
    }

    private static Exceptional<ResourceEntry> callCommandWithEvents(
            CommandSource sender,
            CommandContext context,
            String command,
            AbstractRegistrationContext registrationContext
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
        return Exceptional.none();
    }

    @Override
    public void register(Class<?>... types) {
        for (Class<?> type : types) {
            if (null == type) continue;
            List<AbstractRegistrationContext> contexts = this.createContexts(type);

            for (AbstractRegistrationContext context : contexts) {
                for (String alias : context.getAliases()) {

                    if (context instanceof CommandInheritanceContext
                            && DefaultCommandBus.prepareInheritanceContext(context, alias)) continue;

                    if (DefaultCommandBus.registrations.containsKey(alias))
                        Hartshorn.log().warn("Registering duplicate alias '" + alias + "'");

                    DefaultCommandBus.registrations.put(alias, context);
                }
            }
        }
    }

    private List<AbstractRegistrationContext> createContexts(Class<?> parent) {
        List<AbstractRegistrationContext> contexts = HartshornUtils.emptyList();

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
        Collection<Method> nonInheritedMethods = Reflect.annotatedMethods(parent, Command.class, c -> !c.inherit() || !isParentRegistration);

        nonInheritedMethods.forEach(method -> {
            MethodCommandContext context = DefaultCommandBus.extractNonInheritedContext(method);
            if (null != context) contexts.add(context);
        });

        return contexts;
    }

    @SuppressWarnings("checkstyle:Indentation")
    private static boolean prepareInheritanceContext(AbstractRegistrationContext context, String alias) {
        /*
        If the command 'extends' on the provided alias(es), it should not be registered directly and should instead be
        added to existing registrations. If no existing registration is present (yet), it should be queued so that it
        can be added when the target registration is created.
        */
        if (context.getCommand().extend()) {
            if (DefaultCommandBus.registrations.containsKey(alias))
                DefaultCommandBus.addExtendingAliasToRegistration(
                        alias, (CommandInheritanceContext) context);
            else DefaultCommandBus.queueAliasRegistration(alias, (CommandInheritanceContext) context);

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
                            DefaultCommandBus.addExtendingContextToRegistration(extendingContext, (CommandInheritanceContext) context)
                    );
        }
        return false;
    }

    private CommandInheritanceContext extractCommandInheritanceContext(Class<?> parent) {
        Command command = parent.getAnnotation(Command.class);
        CommandInheritanceContext context = new CommandInheritanceContext(command, this.resources.getMissingArguments(), parent);

        /*
        Inherited methods are only stored inside the CommandInheritanceContext and not in
        DefaultCommandBus#registrations. This ensures no conflicts exist when registering sub-commands with equal
        aliases for different parents.
        */
        @NotNull
        @Unmodifiable
        Collection<Method> inheritedMethods = Reflect.annotatedMethods(parent, Command.class, Command::inherit);
        inheritedMethods.forEach(method -> context.addInheritedCommand(
                DefaultCommandBus.extractInheritedContext(method, context))
        );

        return context;
    }

    private @Nullable
    static MethodCommandContext extractNonInheritedContext(Method method) {
        Command command = method.getAnnotation(Command.class);
        if (command.inherit() && method.getDeclaringClass().isAnnotationPresent(Command.class))
            return null;

        return new MethodCommandContext(command, method);
    }

    private static void addExtendingAliasToRegistration(String alias, CommandInheritanceContext extendingContext) {
        AbstractRegistrationContext context = DefaultCommandBus.registrations.get(alias);
        DefaultCommandBus.addExtendingContextToRegistration(extendingContext, (CommandInheritanceContext) context);
        DefaultCommandBus.registrations.put(alias, context);
    }

    private static void queueAliasRegistration(String alias, CommandInheritanceContext context) {
        List<CommandInheritanceContext> contexts = DefaultCommandBus.queuedAliases.getOrDefault(alias, HartshornUtils.emptyConcurrentList());
        contexts.add(context);
        DefaultCommandBus.queuedAliases.put(alias, contexts);
    }

    private static void addExtendingContextToRegistration(CommandInheritanceContext extendingContext, CommandInheritanceContext context) {
        extendingContext.getInheritedCommands().forEach(context::addInheritedCommand);
    }

    private @Nullable
    static MethodCommandContext extractInheritedContext(Method method, CommandInheritanceContext parentContext) {
        Command command = method.getAnnotation(Command.class);
        if (!command.inherit()) return null;
        for (String alias : command.value()) {
            if (parentContext.getInheritedCommands().stream().anyMatch(cmd -> cmd.getAliases().contains(alias))) {
                /*
                If a parent has two sub-commands with the same alias(es), a warning is thrown. Typically methods calling
                this method will overwrite any existing sub-commands with the generated context.
                */
                Hartshorn.log().warn("Context for '" + parentContext.getPrimaryAlias() + "' has duplicate inherited context for '" + alias + "'.");
            }
        }
        return new MethodCommandContext(command, method);
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
            if (abstractCommand instanceof MethodCommandContext) {
                MethodCommandContext methodContext = (MethodCommandContext) abstractCommand;
                if (!methodContext.getCommand().inherit()
                        || !methodContext.getDeclaringClass().isAnnotationPresent(Command.class)) {
                    spec = this.buildContextExecutor(methodContext, alias);
                }
                else {
                    Hartshorn.log().error("Found direct method registration of inherited command! " + methodContext.getLocation());
                }

            }
            else if (abstractCommand instanceof CommandInheritanceContext) {
                CommandInheritanceContext inheritanceContext = (CommandInheritanceContext) abstractCommand;
                spec = this.buildInheritedContextExecutor(inheritanceContext, alias);
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

    protected static Map<String, AbstractRegistrationContext> getRegistrations() {
        return DefaultCommandBus.registrations;
    }

    protected abstract E buildContextExecutor(AbstractRegistrationContext context, String alias);

    protected abstract E buildInheritedContextExecutor(CommandInheritanceContext context, String alias);

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
        return Exceptional.none();
    }

    protected ArgumentValue<?> generateArgumentValue(String argumentDefinition, String defaultPermission) {
        String type = DefaultCommandBus.DEFAULT_TYPE;
        String key;
        String permission = defaultPermission;
        Matcher elementValue = DefaultCommandBus.ELEMENT_VALUE.matcher(argumentDefinition);
        if (!elementValue.matches() || 0 == elementValue.groupCount())
            Except.handle("Unknown argument specification " + argumentDefinition + ", use Type or Name{Type} or Name{Type:Permission}");

        /*
        Group one specifies either the name of the value (if two or more groups are matched), or the type if only one
        group matched.
        */
        if (1 <= elementValue.groupCount()) {
            String g1 = elementValue.group(1);
            if (1 == elementValue.groupCount()) type = g1;
            key = g1;
        }
        else throw new IllegalArgumentException("Missing key argument in specification '" + argumentDefinition + "'");

        /*
        Group two matches the type if two or more groups are present. This overwrites the default value if applicable.
        */
        if (2 <= elementValue.groupCount() && null != elementValue.group(2))
            type = elementValue.group(2);

        /*
        Group three matches the permission if three groups are present. If the third group is not present, the default
        permission is used. Usually the default permission is provided by the original command registration (which
        defaults to HartshornInformation#GLOBAL_OVERRIDE if none is explicitly specified).
        */
        if (3 <= elementValue.groupCount() && null != elementValue.group(3))
            permission = elementValue.group(3);

        try {
            return this.getArgumentValue(type, permission, key);
        }
        catch (IllegalArgumentException e) {
            return this.getArgumentValue(DefaultCommandBus.DEFAULT_TYPE, permission, key);
        }
    }

    protected List<AbstractArgumentElement<?>> parseArgumentElements(CharSequence argString, String defaultPermission) {
        List<AbstractArgumentElement<?>> elements = HartshornUtils.emptyList();
        AbstractFlagCollection<?> flagCollection = null;

        Matcher genericArgumentMatcher = GENERIC_ARGUMENT.matcher(argString);
        while (genericArgumentMatcher.find()) {

            String part = genericArgumentMatcher.group();
            Matcher argumentMatcher = ARGUMENT.matcher(part);
            if (argumentMatcher.matches()) {
                this.extractArguments(elements, argumentMatcher, defaultPermission);

            }
            else {
                Matcher flagMatcher = FLAG.matcher(part);
                flagCollection = this.getAbstractFlagCollection(flagCollection, flagMatcher, defaultPermission);
            }
        }

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

    private void extractArguments(Collection<AbstractArgumentElement<?>> elements, MatchResult argumentMatcher, String defaultPermission) {
        boolean optional = '[' == argumentMatcher.group(1).charAt(0);
        String elementValue = argumentMatcher.group(2);

        List<AbstractArgumentElement<?>> result = this.parseArgumentElements(elementValue, defaultPermission);
        if (result.isEmpty()) {
            ArgumentValue<?> argumentValue = this.generateArgumentValue(argumentMatcher.group(2), defaultPermission);
            AbstractArgumentElement<?> argumentElement = argumentValue.getElement();
            result = HartshornUtils.asList(argumentElement);
        }

        /*
        If the elements are of one group they should be wrapped into a single element so it can be checked as a group.
        If there is only one element present this may simply return a unwrapped version of the element list.
        */
        AbstractArgumentElement<?> argumentElement = this.wrapElements(result);
        if (optional) {
            elements.add(argumentElement.asOptional());
        }
        else {
            elements.add(argumentElement);
        }
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