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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.Subject;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.commands.CommandExecutor;
import org.dockbox.hartshorn.commands.CommandParser;
import org.dockbox.hartshorn.commands.CommandResources;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.events.CommandEvent;
import org.dockbox.hartshorn.commands.events.CommandEvent.Before;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.DefaultCarrierContext;
import org.dockbox.hartshorn.core.context.element.AnnotatedElementContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.events.parents.Cancellable;
import org.dockbox.hartshorn.i18n.common.Message;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Simple implementation of {@link CommandExecutorContext} targeting {@link Method} based executors.
 */
@Getter(AccessLevel.PROTECTED)
@Posting({ CommandEvent.Before.class, CommandEvent.After.class })
public class MethodCommandExecutorContext<T> extends DefaultCarrierContext implements CommandExecutorContext {

    @Getter private final ApplicationContext applicationContext;
    private final MethodContext<?, T> method;
    private final TypeContext<T> type;
    private final List<String> parentAliases;
    private final Command command;
    @Nullable
    private final Command parent;
    private final boolean isChild;

    @Getter(AccessLevel.NONE)
    private Map<String, CommandParameterContext> parameters;

    public MethodCommandExecutorContext(final ApplicationContext context, final MethodContext<?, T> method, final TypeContext<T> type) {
        super(context);
        final Exceptional<Command> annotated = method.annotation(Command.class);
        if (annotated.absent()) {
            throw new IllegalArgumentException("Provided method is not a command handler");
        }
        this.applicationContext = context;
        this.method = method;
        this.type = type;
        this.command = annotated.get();

        final Exceptional<Command> annotation = type.annotation(Command.class);
        if (annotation.present()) {
            this.parent = annotation.get();
            this.isChild = true;
        }
        else {
            this.parent = null;
            this.isChild = false;
        }

        this.add(new CommandDefinitionContextImpl(this.applicationContext(), this.command(), this.method()));

        this.parentAliases = HartshornUtils.emptyList();
        if (this.parent != null) {
            context.log().debug("Parent for executor context of " + method.qualifiedName() + " found, including parent aliases");
            this.parentAliases.addAll(HartshornUtils.asList(this.parent.value()));
        }
        this.parameters = this.parameters();
    }

    private Map<String, CommandParameterContext> parameters() {
        if (this.parameters == null) {
            this.parameters = HartshornUtils.emptyMap();
            final LinkedList<ParameterContext<?>> parameters = this.method().parameters();
            for (int i = 0; i < parameters.size(); i++) {
                final ParameterContext<?> parameter = parameters.get(i);
                this.parameters.put(parameter.name(), new CommandParameterContext(parameter, i));
            }

        }
        return this.parameters;
    }

    @Override
    public CommandExecutor executor() {
        return (ctx) -> {
            final Cancellable before = new Before(ctx.source(), ctx).with(this.applicationContext()).post();
            if (before.cancelled()) {
                this.applicationContext().log().debug("Execution cancelled for " + this.method().qualifiedName());
                final Message cancelled = this.applicationContext().get(CommandResources.class).cancelled();
                ctx.source().send(cancelled);
                return;
            }

            final T instance = this.applicationContext().get(this.type());
            final List<Object> arguments = this.arguments(ctx);
            this.applicationContext().log().debug("Invoking command method %s with %d arguments".formatted(this.method().qualifiedName(), arguments.size()));
            this.method().invoke(instance, arguments.toArray()).caught(error -> Except.handle("Encountered unexpected error while performing command executor", error));
            new CommandEvent.After(ctx.source(), ctx).with(this.applicationContext()).post();
        };
    }

    @Override
    public boolean accepts(final String command) {
        final CommandDefinitionContext context = this.definition();
        return context.matches(this.strip(command, true));
    }

    @Override
    public String strip(String command, final boolean parentOnly) {
        command = this.stripAny(command, this.parentAliases);
        if (!parentOnly) command = this.stripAny(command, this.definition().aliases());
        return command;
    }

    @Override
    public List<String> aliases() {
        final List<String> aliases = HartshornUtils.emptyList();
        for (final String parentAlias : this.parentAliases()) {
            for (final String alias : this.command().value()) {
                aliases.add(parentAlias + ' ' + alias);
            }
        }
        if (aliases.isEmpty()) {
            aliases.add(this.method().name());
        }
        return aliases;
    }

    @Override
    public TypeContext<?> parent() {
        return this.type;
    }

    @Override
    public AnnotatedElementContext<Method> element() {
        return this.method();
    }

    @Override
    public List<String> suggestions(final CommandSource source, final String command, final CommandParser parser) {
        final String stripped = this.strip(command, false);
        this.applicationContext().log().debug("Collecting suggestions for stripped input %s (was %s)".formatted(stripped, command));
        final List<CommandElement<?>> elements = this.definition().elements();
        final List<String> tokens = HartshornUtils.asList(stripped.split(" "));
        if (command.endsWith(" ") && !"".equals(tokens.get(tokens.size() - 1))) tokens.add("");

        CommandElement<?> last = null;
        for (final CommandElement<?> element : elements) {
            int size = element.size();
            if (size == -1) return HartshornUtils.emptyList();

            if (tokens.size() <= size) {
                last = element;
                break;
            }

            while (size != 0) {
                tokens.remove(0);
                size--;
            }
        }

        if (last == null) {
            this.applicationContext().log().debug("Could not locate last command element to collect suggestions");
            return HartshornUtils.emptyList();
        }
        final Collection<String> suggestions = last.suggestions(source, String.join(" ", tokens));
        this.applicationContext().log().debug("Found " + suggestions.size() + " suggestions");
        return HartshornUtils.asUnmodifiableList(suggestions);
    }

    private CommandDefinitionContext definition() {
        final Exceptional<CommandDefinitionContext> definition = this.first(CommandDefinitionContext.class);
        if (definition.absent()) throw new IllegalStateException("Definition context was lost!");
        return definition.get();
    }

    private String stripAny(String command, final Iterable<String> aliases) {
        for (final String alias : aliases) {
            // Equality is expected when no required arguments are present afterwards
            if (command.equals(alias)) command = "";
            else if (command.startsWith(alias + ' ')) command = command.substring(alias.length() + 1);
        }
        return command;
    }

    private List<Object> arguments(final CommandContext context) {
        final List<Object> arguments = HartshornUtils.list(this.parameters().size());
        final Map<String, CommandParameterContext> parameters = this.parameters();

        for (final Entry<String, CommandParameterContext> entry : parameters.entrySet()) {
            final CommandParameterContext commandParameterContext = entry.getValue();
            final int index = commandParameterContext.index();
            if (commandParameterContext.is(CommandContext.class)) arguments.set(index, context);
            else {
                @Nullable final Object object = context.get(entry.getKey());
                // Target comparison is done last as this can target either the command source, or a parameter target
                if (object == null && commandParameterContext.is(Subject.class)) arguments.set(index, context.source());
                else arguments.set(index, object);
            }
        }
        return arguments;
    }
}
