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

package org.dockbox.hartshorn.commands.beta.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.beta.api.CommandContainerContext;
import org.dockbox.hartshorn.commands.beta.api.CommandExecutor;
import org.dockbox.hartshorn.commands.beta.api.CommandExecutorContext;
import org.dockbox.hartshorn.commands.beta.api.CommandGateway;
import org.dockbox.hartshorn.commands.beta.api.CommandParser;
import org.dockbox.hartshorn.commands.beta.api.ParsedContext;
import org.dockbox.hartshorn.commands.beta.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;

import lombok.AccessLevel;
import lombok.Getter;

@Singleton
public class SimpleCommandGateway implements CommandGateway {

    @Wired
    private CommandParser parser;

    @Getter(AccessLevel.PROTECTED)
    private final transient Multimap<String, CommandExecutorContext> contexts = ArrayListMultimap.create();

    @Override
    public void accept(CommandSource source, String command) throws ParsingException {
        final String alias = command.split(" ")[0];
        for (CommandExecutorContext context : this.contexts.get(alias)) {
            if (context.accepts(command)) {
                final Exceptional<ParsedContext> commandContext = this.parser.parse(command, source, context);
                if (commandContext.present()) {
                    context.executor().execute(commandContext.get());
                    return;
                }
            }
        }
        throw new IllegalArgumentException("No supported command handler found for '" + command + "'");
    }

    @Override
    public void accept(CommandContext context) {
        final CommandExecutor executor = this.get(context);
        if (executor != null) executor.execute(context);
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
            this.contexts.put(alias, context);
        }
    }

    private void register(Method method, Class<?> type) {
        this.register(new MethodCommandExecutorContext(method, type));
    }

    @Override
    public CommandExecutor get(CommandContext context) {
        for (CommandExecutorContext executorContext : this.getContexts().get(context.alias())) {
            if (executorContext.accepts(context.command())) return executorContext.executor();
        }
        return null;
    }
}
