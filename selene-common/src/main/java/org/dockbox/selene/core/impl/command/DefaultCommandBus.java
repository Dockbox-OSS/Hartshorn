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

import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.annotations.command.Command;
import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.impl.command.registration.AbstractRegistrationContext;
import org.dockbox.selene.core.impl.command.registration.CommandInheritanceContext;
import org.dockbox.selene.core.impl.command.registration.MethodCommandContext;
import org.dockbox.selene.core.impl.command.values.AbstractArgumentElement;
import org.dockbox.selene.core.impl.command.values.AbstractArgumentValue;
import org.dockbox.selene.core.impl.command.values.AbstractFlagCollection;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DefaultCommandBus implements CommandBus {

    /**
     Represents the default type for command elements matched by {@link DefaultCommandBus#FLAG} or
     {@link DefaultCommandBus#ARGUMENT}. If no type is defined in those matches, this value is used. 'String' is used
     as this is the base value provided to Selene, thus requiring no further converting to other data types.
     */
    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final String DEFAULT_TYPE = "String";

    /**
     Each matching element represents either a flag or argument, these can then be parsed using
     {@link DefaultCommandBus#FLAG} and {@link DefaultCommandBus#ARGUMENT}.
     */
    private static final Pattern GENERIC_ARGUMENT = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))");

    /**
     Each matching element represents a flag with either one or two groups. The first group (G1) is required, and
     indicates the name of the flag. The second group (G2) is optional, and represents the value expected by the flag.
     G2 is a argument which can be parsed using {@link DefaultCommandBus#ARGUMENT}.
     <p>
     Syntax:
     - Without value: -f, --flag
     - With simple value: -f Type, --flag Type
     - With permission value: -f Type:Permission, --flag Type:Permission
     */
    private static final Pattern FLAG = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?");

    /**
     Each matching element represents a argument with two groups. The first group (G1) indicates whether the argument
     is required or optional. The second group can either be a argument meta which can be parsed using
     {@link DefaultCommandBus#ELEMENT_VALUE}, or a simple value if {@link DefaultCommandBus#ELEMENT_VALUE} returns no
     matches. Arguments can be grouped.
     <p>
     Syntax:
     - Optional without type: [Argument]
     - Optional with simple value: [Argument{Type}]
     - Optional with permission value: [Argument{Type:Permission}]
     - Required without type: &lt;Argument&gt;
     - Required with value is equal in syntax to optional, but wrapped in &lt;&gt;
     - Argument group: [&lt;Argument&gt; &lt;Argument{Type}&gt;]
     */
    private static final Pattern ARGUMENT = Pattern.compile("([\\[<])(.+)[\\]>]");

    /**
     Each matching element represents additional meta information for matching elements of
     {@link DefaultCommandBus#ARGUMENT}. Matches contain either one or two groups. If both groups are present, group 1
     represents the name of the argument, and group 2 represents the value. If only group 1 is present, it represents
     the type of the argument and the name is obtained from the argument definition.
     <p>
     Syntax:
     - Type
     - Name{Type}
     - Name{Type:Permission}
     */
    private static final Pattern ELEMENT_VALUE = Pattern.compile("(\\w+)(?:\\{(\\w+)(?::([\\w\\.]+))?\\})?");

    private final Map<String, AbstractRegistrationContext> registrations = SeleneUtils.emptyConcurrentMap();
    private final Map<String, ConfirmableQueueItem> confirmQueue = SeleneUtils.emptyConcurrentMap();
    private final Map<String, List<CommandInheritanceContext>> queuedAliases = SeleneUtils.emptyConcurrentMap();

    @Override
    public void register(Object... objs) {
        for (Object obj : objs) {
            if (null == obj) continue;
            if (!(obj instanceof Class<?>)) obj = obj.getClass();
            List<AbstractRegistrationContext> contexts = this.createContexts((Class<?>) obj);

            for (AbstractRegistrationContext context : contexts) {
                for (String alias : context.getAliases()) {

                    if (context instanceof CommandInheritanceContext
                            && this.prepareInheritanceContext(context, alias)) continue;

                    if (this.registrations.containsKey(alias))
                        Selene.log().warn("Registering duplicate alias '" + alias + "'");

                    this.registrations.put(alias, context);
                }
            }
        }
    }

    private boolean prepareInheritanceContext(AbstractRegistrationContext context, String alias) {
        /*
         If the command 'extends' on the provided alias(es), it should not be registered directly and should instead be
         added to existing registrations. If no existing registration is present (yet), it should be queued so that it
         can be added when the target registration is created.
         */
        if (context.getCommand().extend()) {
            if (this.registrations.containsKey(alias))
                this.addExtendingAliasToRegistration(alias, (CommandInheritanceContext) context);
            else this.queueAliasRegistration(alias, (CommandInheritanceContext) context);

            return true;
        } else {
            /*
             If the command does not extend on the provided alias(es), it can be registered directly. These
             registrations can still be added to if future registrations extend aliases defined in this registration.
             However, once #apply is called these registrations are no longer mutable.
             */
            this.queuedAliases
                    .getOrDefault(alias, SeleneUtils.emptyConcurrentList())
                    .forEach(extendingContext ->
                            this.addExtendingContextToRegistration(
                                    extendingContext,
                                    (CommandInheritanceContext) context)
                    );
        }
        return false;
    }

    private void addExtendingAliasToRegistration(String alias, CommandInheritanceContext extendingContext) {
        AbstractRegistrationContext context = this.registrations.get(alias);
        this.addExtendingContextToRegistration(extendingContext, (CommandInheritanceContext) context);
        this.registrations.put(alias, context);
    }

    private void addExtendingContextToRegistration(CommandInheritanceContext extendingContext, CommandInheritanceContext context) {
        extendingContext.getInheritedCommands().forEach(context::addInheritedCommand);
    }

    private void queueAliasRegistration(String alias, CommandInheritanceContext context) {
        List<CommandInheritanceContext> contexts =
                this.queuedAliases.getOrDefault(alias, SeleneUtils.emptyConcurrentList());
        contexts.add(context);
        this.queuedAliases.put(alias, contexts);
    }

    protected void clearAliasQueue() {
        // This should be called after #apply has finished, to avoid unexpected alias extensions.
        this.queuedAliases.clear();
    }

    private List<AbstractRegistrationContext> createContexts(Class<?> parent) {
        List<AbstractRegistrationContext> contexts = SeleneUtils.emptyList();

        /*
         It is possible the class itself is not annotated with @Command, in which case all methods should be registered
         as individual commands. If the class itself is annotated with @Command, only methods marked as non-inherited
         should be registered as individual commands, and using all other methods annotated with @Command as
         sub-commands of the command defined in the class annotation.
         */
        boolean isParentRegistration = parent.isAnnotationPresent(Command.class);
        if (isParentRegistration)
            contexts.add(this.extractCommandInheritanceContext(parent));

        @NotNull @Unmodifiable Collection<Method> nonInheritedMethods =
                SeleneUtils.getAnnotedMethods(parent, Command.class, c -> !c.inherit() || isParentRegistration);
        nonInheritedMethods.forEach(method -> contexts.add(this.extractNonInheritedContext(method)));

        return contexts;
    }

    private CommandInheritanceContext extractCommandInheritanceContext(Class<?> parent) {
        Command command = parent.getAnnotation(Command.class);
        CommandInheritanceContext context = new CommandInheritanceContext(command);

        /*
         Inherited methods are only stored inside the CommandInheritanceContext and not in
         DefaultCommandBus#registrations. This ensures no conflicts exist when registering sub-commands with equal
         aliases for different parents.
         */
        @NotNull @Unmodifiable Collection<Method> inheritedMethods =
                SeleneUtils.getAnnotedMethods(parent, Command.class, Command::inherit);
        inheritedMethods.forEach(method ->
                context.addInheritedCommand(this.extractInheritedContext(method, context))
        );

        return context;
    }

    private @Nullable MethodCommandContext extractInheritedContext(Method method, CommandInheritanceContext parentContext) {
        Command command = method.getAnnotation(Command.class);
        if (!command.inherit()) return null;
        for (String alias : command.aliases()) {
            if (parentContext.getInheritedCommands()
                    .stream()
                    .anyMatch(cmd -> cmd.getAliases().contains(alias))
            ) {
                /*
                 If a parent has two sub-commands with the same alias(es), a warning is thrown. Typically methods calling
                 this method will overwrite any existing sub-commands with the generated context.
                 */
                Selene.log().warn("Context for '" + parentContext.getPrimaryAlias() + "' has duplicate inherited context for '" + alias + "'.");
            }
        }
        return new MethodCommandContext(command, method);
    }

    private @Nullable MethodCommandContext extractNonInheritedContext(Method method) {
        Command command = method.getAnnotation(Command.class);
        if (command.inherit()) return null;
        return new MethodCommandContext(command, method);
    }

    public Exceptional<Boolean> confirmCommand(String confirmId) {
        if (this.confirmQueue.containsKey(confirmId)) {
            ConfirmableQueueItem confirmableQueueItem = this.confirmQueue.get(confirmId);
            if (confirmableQueueItem.getSource() instanceof CommandSource) {
                confirmableQueueItem.getCommand().call(
                        (CommandSource) confirmableQueueItem.getSource(),
                        confirmableQueueItem.getContext()
                );
                return Exceptional.of(true);
            }
        }
        return Exceptional.empty();
    }

    protected AbstractArgumentValue<?> generateArgumentValue(String argumentDefinition, String defaultPermission) {
        String type = DefaultCommandBus.DEFAULT_TYPE;
        String key;
        String permission = defaultPermission;
        Matcher argumentMetaMatcher = DefaultCommandBus.ELEMENT_VALUE.matcher(argumentDefinition);
        if (!argumentMetaMatcher.matches() || 0 == argumentMetaMatcher.groupCount())
            Selene.getServer().except("Unknown argument specification " + argumentDefinition + ", use Type or Name{Type} or Name{Type:Permission}");

        /*
         Group one specifies either the name of the value (if two or more groups are matched), or the type if only one
         group matched.
         */
        if (1 <= argumentMetaMatcher.groupCount()) {
            if (1 == argumentMetaMatcher.groupCount()) type = argumentMetaMatcher.group(1);
            key = argumentMetaMatcher.group(1);
        } else throw new IllegalArgumentException("Missing key argument in specification '" + argumentDefinition + "'");

        /*
         Group two matches the type if two or more groups are present. This overwrites the default value if applicable.
         */
        if (2 <= argumentMetaMatcher.groupCount()) type = argumentMetaMatcher.group(2);

        /*
         Group three matches the permission if three groups are present. If the third group is not present, the default
         permission is used. Usually the default permission is provided by the original command registration (which
         defaults to Selene#GLOBAL_OVERRIDE if none is explicitly specified).
         */
        if (3 <= argumentMetaMatcher.groupCount()) permission = argumentMetaMatcher.group(3);

        return this.generateArgumentValue(type, key, permission);
    }

    protected List<AbstractArgumentElement<?>> parseArgumentElements(CharSequence argString, String defaultPermission) {
        List<AbstractArgumentElement<?>> elements = SeleneUtils.emptyList();
        AbstractFlagCollection<?> flagCollection = null;

        Matcher genericArgumentMatcher = GENERIC_ARGUMENT.matcher(argString);
        while (genericArgumentMatcher.find()) {

            String part = genericArgumentMatcher.group();
            Matcher argumentMatcher = ARGUMENT.matcher(part);
            if (argumentMatcher.matches()) {
                this.extractArguments(elements, argumentMatcher, defaultPermission);

            } else {
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

    private AbstractFlagCollection<?> getAbstractFlagCollection(AbstractFlagCollection<?> flagCollection, Matcher flagMatcher, String defaultPermission) {
        if (flagMatcher.matches()) {
            if (null == flagCollection) flagCollection = this.createEmptyFlagCollection();
            this.parseFlag(flagCollection, flagMatcher.group(1), flagMatcher.group(2), defaultPermission);
        }
        return flagCollection;
    }

    private void extractArguments(Collection<AbstractArgumentElement<?>> elements, Matcher argumentMatcher, String defaultPermission) {
        boolean optional = '[' == argumentMatcher.group(1).charAt(0);
        String elementValue = argumentMatcher.group(2);

        List<AbstractArgumentElement<?>> result = this.parseArgumentElements(elementValue, defaultPermission);
        if (result.isEmpty()) {
            AbstractArgumentValue<?> argumentValue = this.generateArgumentValue(argumentMatcher.group(2), defaultPermission);
            AbstractArgumentElement<?> argumentElement = argumentValue.getElement();
            result = SeleneUtils.asList(argumentElement);
        }

        /*
         If the elements are of one group they should be wrapped into a single element so it can be checked as a group.
         If there is only one element present this may simply return a unwrapped version of the element list.
         */
        AbstractArgumentElement<?> argumentElement = this.wrapElements(result);
        if (optional) {
            elements.add(argumentElement.asOptional());
        } else {
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
            } else {
                flags.addNamedFlag(name);
            }

        } else {
            AbstractArgumentValue<?> argumentValue = this.generateArgumentValue(value, defaultPermission);
            if (0 <= name.indexOf(':')) {
                Selene.getServer().except("Flag values do not support permissions at flag `" + name + "`. Permit the value instead");
            }
            flags.addValueBasedFlag(name, argumentValue);
        }
    }

    protected abstract AbstractArgumentElement<?> wrapElements(List<AbstractArgumentElement<?>> elements);

    protected abstract AbstractArgumentValue<?> generateArgumentValue(String type, String permission, String key);

    protected abstract AbstractFlagCollection<?> createEmptyFlagCollection();

    protected Map<String, AbstractRegistrationContext> getRegistrations() {
        return this.registrations;
    }

}
