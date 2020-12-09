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

    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final String DEFAULT_TYPE = "String";
    private static final Pattern GENERIC_ARGUMENT = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))"); //each match is a flag or argument
    private static final Pattern FLAG = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?"); //g1: name  (g2: value)
    private static final Pattern ARGUMENT = Pattern.compile("([\\[<])(.+)[\\]>]"); //g1: <[  g2: run argFinder, if nothing it's a value
    private static final Pattern ARGUMENT_META = Pattern.compile("(\\w+)(?:\\{(\\w+)(?::([\\w\\.]+))?\\})?"); //g1: name  g2: if present type, other wise use g1
    private static final Pattern SUBCOMMAND = Pattern.compile("[a-z]*");

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
        if (context.getCommand().extend()) {
            if (this.registrations.containsKey(alias))
                this.addExtendingAliasToRegistration(alias, (CommandInheritanceContext) context);
            else this.queueAliasRegistration(alias, (CommandInheritanceContext) context);

            return true;
        } else {
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
        this.queuedAliases.clear();
    }

    private List<AbstractRegistrationContext> createContexts(Class<?> parent) {
        List<AbstractRegistrationContext> contexts = SeleneUtils.emptyList();
        if (parent.isAnnotationPresent(Command.class))
            contexts.add(this.extractCommandInheritanceContext(parent));

        @NotNull @Unmodifiable Collection<Method> nonInheritedMethods =
                SeleneUtils.getAnnotedMethods(parent, Command.class, c -> !c.inherit());
        nonInheritedMethods.forEach(method -> contexts.add(this.extractNonInheritedContext(method)));

        return contexts;
    }

    private CommandInheritanceContext extractCommandInheritanceContext(Class<?> parent) {
        Command command = parent.getAnnotation(Command.class);
        CommandInheritanceContext context = new CommandInheritanceContext(command);

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

    protected AbstractArgumentValue<?> generateGenericArgument(String argumentDefinition) {
        String type;
        String key;
        String permission;
        Matcher argumentMetaMatcher = DefaultCommandBus.ARGUMENT_META.matcher(argumentDefinition);
        if (!argumentMetaMatcher.matches() || 0 == argumentMetaMatcher.groupCount())
            Selene.getServer().except("Unknown argument specification " + argumentDefinition + ", use Type or Name{Type} or Name{Type:Permission}");

        if (1 <= argumentMetaMatcher.groupCount()) key = argumentMetaMatcher.group(1);
        else throw new IllegalArgumentException("Missing key argument in specification '" + argumentDefinition + "'");

        if (2 <= argumentMetaMatcher.groupCount()) type = argumentMetaMatcher.group(2);
        else type = DefaultCommandBus.DEFAULT_TYPE;

        if (3 <= argumentMetaMatcher.groupCount()) permission = argumentMetaMatcher.group(3);
        else permission = Selene.GLOBAL_BYPASS.get();

        return this.generateGenericArgument(type, key, permission);
    }

    protected List<AbstractArgumentElement<?>> parseArgumentElements(CharSequence argString) {
        List<AbstractArgumentElement<?>> elements = SeleneUtils.emptyList();
        AbstractFlagCollection<?> flagCollection = null;
        Matcher genericArgumentMatcher = GENERIC_ARGUMENT.matcher(argString);
        while (genericArgumentMatcher.find()) {
            String part = genericArgumentMatcher.group();
            Matcher argumentMatcher = ARGUMENT.matcher(part);
            if (argumentMatcher.matches()) {
                this.extractArguments(elements, argumentMatcher);
            } else {
                Matcher flagMatcher = FLAG.matcher(part);
                flagCollection = this.getAbstractFlagCollection(flagCollection, flagMatcher);
            }
        }
        if (null == flagCollection) return elements;
        else return flagCollection.buildAndCombines(this.wrapElements(elements));
    }

    private AbstractFlagCollection<?> getAbstractFlagCollection(AbstractFlagCollection<?> flagCollection, Matcher flagMatcher) {
        if (flagMatcher.matches()) {
            if (null == flagCollection) flagCollection = this.createEmptyFlagCollection();
            this.parseFlag(flagCollection, flagMatcher.group(1), flagMatcher.group(2));
        }
        return flagCollection;
    }

    private void extractArguments(List<AbstractArgumentElement<?>> elements, Matcher argumentMatcher) {
        boolean optional = '[' == argumentMatcher.group(1).charAt(0);
        String argumentMeta = argumentMatcher.group(2);
        List<AbstractArgumentElement<?>> result = this.parseArgumentElements(argumentMeta);
        if (result.isEmpty()) {
            AbstractArgumentValue<?> argumentValue = this.generateGenericArgument(argumentMatcher.group(2));
            AbstractArgumentElement<?> argumentElement = argumentValue.getElement();
            result = SeleneUtils.asList(argumentElement);
        }
        AbstractArgumentElement<?> argumentElement = this.wrapElements(result);
        if (optional) {
            elements.add(argumentElement.asOptional());
        } else {
            elements.add(argumentElement);
        }
    }

    private void parseFlag(AbstractFlagCollection<?> flags, String name, String value) {
        if (null == value) {
            int at;
            if (0 <= (at = name.indexOf(':'))) {
                name = name.substring(0, at);
                String permission = name.substring(at + 1);
                flags.addNamedPermissionFlag(name, permission);
            } else {
                flags.addNamedFlag(name);
            }
        } else {
            AbstractArgumentValue<?> argumentValue = this.generateGenericArgument(value);
            if (0 <= name.indexOf(':')) {
                Selene.getServer().except("Flag values do not support permissions at flag `" + name + "`. Permit the value instead");
            }
            flags.addValueBasedFlag(name, argumentValue);
        }
    }

    protected abstract AbstractArgumentElement<?> wrapElements(List<AbstractArgumentElement<?>> elements);

    protected abstract AbstractArgumentValue<?> generateGenericArgument(String type, String permission, String key);

    protected abstract AbstractFlagCollection<?> createEmptyFlagCollection();

    protected Map<String, AbstractRegistrationContext> getRegistrations() {
        return this.registrations;
    }

}
