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

import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.context.MethodCommandExecutorContext;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.commands.extension.ExtensionResult;
import org.dockbox.hartshorn.core.ArrayListMultiMap;
import org.dockbox.hartshorn.core.Enableable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Simple implementation of {@link CommandGateway}.
 */
@Singleton
@Binds(CommandGateway.class)
public class CommandGatewayImpl implements CommandGateway, Enableable {

    @Getter(AccessLevel.PROTECTED)
    private final transient MultiMap<String, CommandExecutorContext> contexts = new ArrayListMultiMap<>();
    @Getter(AccessLevel.PROTECTED)
    private final transient List<CommandExecutorExtension> extensions = new CopyOnWriteArrayList<>();
    @Inject
    private CommandParser parser;
    @Inject
    private CommandResources resources;
    @Inject
    private ApplicationContext context;

    @Override
    public boolean canEnable() {
        return this.extensions.isEmpty();
    }

    @Override
    public void enable() {
        for (final TypeContext<? extends CommandExecutorExtension> extension : this.context.environment().children(CommandExecutorExtension.class)) {
            this.context.log().debug("Adding extension " + extension.name() + " to command gateway");
            this.add(this.context.get(extension));
        }
    }

    @Override
    public void accept(final CommandSource source, final String command) throws ParsingException {
        final Exceptional<CommandExecutorContext> context = this.lookupContext(command);
        if (context.absent()) throw new ParsingException(this.resources.missingHandler(command));
        else {
            final Exceptional<CommandContext> commandContext = this.parser.parse(command, source, context.get());
            if (commandContext.present()) {
                this.execute(context.get(), commandContext.get());
            }
            else if (commandContext.caught()) {
                commandContext.rethrowUnchecked();
            }
            else {
                this.context.log().warn("Could not parse command for input " + command + " but yielded no exceptions");
            }
        }
    }

    private Exceptional<CommandExecutorContext> lookupContext(final String command) {
        final String alias = command.split(" ")[0];
        CommandExecutorContext bestContext = null;
        this.context.log().debug("Looking up executor context for " + command + " in " + this.contexts.size() + " contexts");
        for (final CommandExecutorContext context : this.contexts.get(alias)) {
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
                if (!result.proceed()) {
                    context.applicationContext().log().debug("Extension " + TypeContext.of(extension).name() + " rejected direct execution, cancelling command executor.");
                    return;
                }
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
    public <T> void register(final Key<T> key) {
        for (final MethodContext<?, T> method : key.type().methods(Command.class)) {
            this.register(method, key);
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
            aliases = List.of(annotated.get().value());
        }
        else if (!container.get().aliases().isEmpty()) {
            aliases = container.get().aliases();
        }
        else {
            throw new IllegalArgumentException("Executor should either be declared in command type or container should provide aliases");
        }

        for (final String alias : aliases) {
            this.contexts().put(alias, context);
        }
        this.context.add(context);
    }

    @Override
    public List<String> suggestions(final CommandSource source, final String command) {
        final Exceptional<CommandExecutorContext> context = this.lookupContext(command);
        final List<String> suggestions = new ArrayList<>();

        if (context.present())
            suggestions.addAll(context.get().suggestions(source, command, this.parser));

        final String alias = command.split(" ")[0];
        final Collection<CommandExecutorContext> contexts = this.contexts().get(alias);
        for (final CommandExecutorContext executorContext : contexts) {
            for (final String contextAlias : executorContext.aliases()) {
                if (contextAlias.startsWith(command)) {
                    final String stripped = contextAlias.replaceFirst(alias + " ", "");
                    if (!"".equals(stripped)) suggestions.add(stripped);
                }
            }
        }

        return Collections.unmodifiableList(suggestions);
    }

    @Override
    public Exceptional<CommandExecutorContext> get(final CommandContext context) {
        for (final CommandExecutorContext executorContext : this.contexts().get(context.alias())) {
            if (executorContext.accepts(context.command())) return Exceptional.of(executorContext);
        }
        return Exceptional.empty();
    }

    @Override
    public void add(final CommandExecutorExtension extension) {
        this.extensions.add(extension);
    }

    private <T> void register(final MethodContext<?, T> method, final Key<T> key) {
        this.register(new MethodCommandExecutorContext<>(this.context, method, key));
    }
}
