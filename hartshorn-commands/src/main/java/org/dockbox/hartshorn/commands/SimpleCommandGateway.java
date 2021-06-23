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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.entry.FakeResource;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContainerContext;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.context.MethodCommandExecutorContext;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.commands.extension.ExtensionResult;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.di.annotations.Binds;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;

import lombok.AccessLevel;
import lombok.Getter;

@Singleton
@Binds(CommandGateway.class)
public class SimpleCommandGateway implements CommandGateway {

    @Wired
    private CommandParser parser;

    private static final transient Multimap<String, CommandExecutorContext> contexts = ArrayListMultimap.create();
    @Getter(AccessLevel.PROTECTED)
    private final transient List<CommandExecutorExtension> extensions = HartshornUtils.emptyConcurrentList();

    private Exceptional<CommandExecutorContext> lookupContext(String command) {
        final String alias = command.split(" ")[0];
        CommandExecutorContext bestContext = null;
        for (CommandExecutorContext context : contexts.get(alias)) {
            if (context.accepts(command)) {
                if (bestContext == null) {
                    bestContext = context;
                } else {
                    final String stripped = context.strip(command, false);
                    // This leaves the arguments without the context's aliases. If the new value is shorter it means more aliases were
                    // stripped, indicating it's providing a deeper level sub-command.
                    if (stripped.length() < bestContext.strip(command, false).length()) {
                        bestContext = context;
                    }
                }
            }
        }
        return Exceptional.of(bestContext);
    }

    protected void execute(CommandExecutorContext context, CommandContext commandContext) {
        for (CommandExecutorExtension extension : this.getExtensions()) {
            if (extension.extend(context)) {
                final ExtensionResult result = extension.execute(commandContext, context);
                if (!result.proceed()) {
                    commandContext.getSender().send(result.reason());
                    return;
                }
            }
        }
        context.executor().execute(commandContext);
    }

    @Override
    public void accept(CommandSource source, String command) throws ParsingException {
        final Exceptional<CommandExecutorContext> context = this.lookupContext(command);
        if (context.absent()) throw new ParsingException(new FakeResource("No supported command handler found for '" + command + "'"));
        else {
            final Exceptional<CommandContext> commandContext = this.parser.parse(command, source, context.get());
            if (commandContext.present()) {
                this.execute(context.get(), commandContext.get());
            }
        }
    }

    @Override
    public void accept(CommandContext context) {
        final CommandExecutorContext executor = this.get(context);
        if (executor != null) this.execute(executor, context);
        else throw new IllegalStateException("No executor registered for command '" + context.alias() + "' with " + context.arguments().size() + " arguments");
    }

    @Override
    public void register(Class<?> type) {
        final Collection<Method> methods = Reflect.annotatedMethods(type, Command.class);
        if (methods.isEmpty()) return;

        for (Method method : methods) this.register(method, type);
    }

    @Override
    public void register(CommandExecutorContext context) {
        final Exceptional<CommandContainerContext> container = context.first(CommandContainerContext.class);
        if (container.absent()) throw new IllegalArgumentException("Executor contexts should contain at least one container context");

        List<String> aliases;
        if (Reflect.isNotVoid(context.parent()) && context.parent().isAnnotationPresent(Command.class)) {
            aliases = HartshornUtils.asUnmodifiableList(context.parent().getAnnotation(Command.class).value());
        } else if (!container.get().aliases().isEmpty()){
            aliases = container.get().aliases();
        } else {
            throw new IllegalArgumentException("Executor should either be declared in command type or container should provide aliases");
        }

        for (String alias : aliases) {
            contexts().put(alias, context);
        }
        Hartshorn.context().add(context);
    }

    private void register(Method method, Class<?> type) {
        this.register(new MethodCommandExecutorContext(method, type));
    }

    @Override
    @UnmodifiableView
    public List<String> suggestions(CommandSource source, String command) {
        final Exceptional<CommandExecutorContext> context = this.lookupContext(command);
        if (context.absent()) return HartshornUtils.emptyList();
        return HartshornUtils.asUnmodifiableList(context.get().suggestions(source, command, this.parser));
    }

    @Override
    public CommandExecutorContext get(CommandContext context) {
        for (CommandExecutorContext executorContext : contexts().get(context.alias())) {
            if (executorContext.accepts(context.command())) return executorContext;
        }
        return null;
    }

    @Override
    public void add(CommandExecutorExtension extension) {
        this.extensions.add(extension);
    }

    public static Multimap<String, CommandExecutorContext> contexts() {
        return SimpleCommandGateway.contexts;
    }
}
