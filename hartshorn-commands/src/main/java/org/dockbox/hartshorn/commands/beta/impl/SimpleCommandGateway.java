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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.beta.api.CommandExecutor;
import org.dockbox.hartshorn.commands.beta.api.CommandGateway;
import org.dockbox.hartshorn.commands.beta.api.CommandParser;
import org.dockbox.hartshorn.commands.beta.api.ParsedContext;
import org.dockbox.hartshorn.commands.beta.api.CommandExecutorContext;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;

public class SimpleCommandGateway implements CommandGateway {

    @Wired
    private CommandParser parser;

    @Getter(AccessLevel.PROTECTED)
    private final transient Set<CommandExecutorContext> contexts = HartshornUtils.emptyConcurrentSet();

    @Override
    public void accept(CommandSource source, String command) {
        for (CommandExecutorContext context : this.contexts) {
            if (context.accepts(command)) {
                final Exceptional<ParsedContext> commandContext = this.parser.parse(command, source, context);
                if (commandContext.present()) {
                    this.accept(source, commandContext.get());
                    return;
                }
            }
        }
        throw new IllegalArgumentException("No supported command handler found for '" + command + "'");
    }

    @Override
    public void accept(CommandSource source, ParsedContext context) {
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
        this.contexts.add(context);
    }

    private void register(Method method, Class<?> type) {
        this.register(new MethodCommandExecutorContext(method, type));
    }

    @Override
    public CommandExecutor get(ParsedContext context) {
        for (CommandExecutorContext executorContext : this.getContexts()) {
            if (executorContext.accepts(context.command())) return executorContext.executor();
        }
        return null;
    }
}
