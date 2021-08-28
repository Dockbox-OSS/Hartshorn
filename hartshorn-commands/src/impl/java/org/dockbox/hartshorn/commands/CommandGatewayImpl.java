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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.context.MethodCommandExecutorContext;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.commands.extension.ExtensionResult;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Simple implementation of {@link CommandGateway}.
 */
@Singleton
@Binds(CommandGateway.class)
public class CommandGatewayImpl implements CommandGateway {

    private static final transient Multimap<String, CommandExecutorContext> contexts = ArrayListMultimap.create();
    @Getter(AccessLevel.PROTECTED)
    private final transient List<CommandExecutorExtension> extensions = HartshornUtils.emptyConcurrentList();
    @Inject
    private CommandParser parser;
    @Inject
    private CommandResources resources;
    @Inject
    private ApplicationContext context;

    @Override
    public void accept(final CommandSource source, final String command) throws ParsingException {
        final Exceptional<CommandExecutorContext> context = this.lookupContext(command);
        if (context.absent()) throw new ParsingException(this.resources.missingHandler(command));
        else {
            final Exceptional<CommandContext> commandContext = this.parser.parse(command, source, context.get());
            if (commandContext.present()) {
                this.execute(context.get(), commandContext.get());
            }
        }
    }

    private Exceptional<CommandExecutorContext> lookupContext(final String command) {
        final String alias = command.split(" ")[0];
        CommandExecutorContext bestContext = null;
        for (final CommandExecutorContext context : contexts.get(alias)) {
            if (context.accepts(command)) {
                if (bestContext == null) {
                    bestContext = context;
                }
                else {
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

    protected void execute(final CommandExecutorContext context, final CommandContext commandContext) {
        for (final CommandExecutorExtension extension : this.extensions()) {
            if (extension.extend(context)) {
                final ExtensionResult result = extension.execute(commandContext, context);
                if (result.send()) commandContext.source().send(result.reason());
                if (!result.proceed()) return;
            }
        }
        context.executor().execute(commandContext);
    }

    @Override
    public void accept(final CommandContext context) throws ParsingException {
        final Exceptional<CommandExecutorContext> executor = this.get(context);
        executor.present(e -> this.execute(e, context))
                .orThrow(() -> new ParsingException(this.resources.missingExecutor(context.alias(), context.arguments().size())));
    }

    @Override
    public void register(final TypeContext<?> type) {
        for (final MethodContext<?, ?> method : type.flatMethods(Command.class)) {
            this.register(method, type);
        }
    }

    @Override
    public void register(final CommandExecutorContext context) {
        final Exceptional<CommandDefinitionContext> container = context.first(CommandDefinitionContext.class);
        if (container.absent()) throw new IllegalArgumentException("Executor contexts should contain at least one container context");

        final List<String> aliases;
        final TypeContext<?> typeContext = context.parent();
        final Exceptional<Command> annotated = typeContext.annotation(Command.class);
        if (!typeContext.isVoid() && annotated.present()) {
            aliases = HartshornUtils.asUnmodifiableList(annotated.get().value());
        }
        else if (!container.get().aliases().isEmpty()) {
            aliases = container.get().aliases();
        }
        else {
            throw new IllegalArgumentException("Executor should either be declared in command type or container should provide aliases");
        }

        for (final String alias : aliases) {
            contexts().put(alias, context);
        }
        this.context.add(context);
    }

    @Override
    @UnmodifiableView
    public List<String> suggestions(final CommandSource source, final String command) {
        final Exceptional<CommandExecutorContext> context = this.lookupContext(command);
        final List<String> suggestions = HartshornUtils.emptyList();

        if (context.present())
            suggestions.addAll(context.get().suggestions(source, command, this.parser));

        final String alias = command.split(" ")[0];
        final Collection<CommandExecutorContext> contexts = contexts().get(alias);
        for (final CommandExecutorContext executorContext : contexts) {
            for (final String contextAlias : executorContext.aliases()) {
                if (contextAlias.startsWith(command)) {
                    final String stripped = contextAlias.replaceFirst(alias + " ", "");
                    if (!"".equals(stripped)) suggestions.add(stripped);
                }
            }
        }

        return HartshornUtils.asUnmodifiableList(suggestions);
    }

    @Override
    public Exceptional<CommandExecutorContext> get(final CommandContext context) {
        for (final CommandExecutorContext executorContext : contexts().get(context.alias())) {
            if (executorContext.accepts(context.command())) return Exceptional.of(executorContext);
        }
        return Exceptional.empty();
    }

    @Override
    public void add(final CommandExecutorExtension extension) {
        this.extensions.add(extension);
    }

    private void register(final MethodContext<?, ?> method, final TypeContext<?> type) {
        this.register(new MethodCommandExecutorContext(this.context, method, type));
    }

    /**
     * Gets all contexts stored by the gateway.
     *
     * @return
     */
    public static Multimap<String, CommandExecutorContext> contexts() {
        return CommandGatewayImpl.contexts;
    }
}
