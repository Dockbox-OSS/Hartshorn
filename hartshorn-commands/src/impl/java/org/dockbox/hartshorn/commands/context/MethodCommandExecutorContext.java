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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.Subject;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.commands.CommandExecutor;
import org.dockbox.hartshorn.commands.CommandParser;
import org.dockbox.hartshorn.commands.CommandResources;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.events.CommandEvent;
import org.dockbox.hartshorn.commands.events.CommandEvent.Before;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.events.parents.Cancellable;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
public class MethodCommandExecutorContext extends DefaultContext implements CommandExecutorContext {

    private final Method method;
    private final Class<?> type;
    private final List<String> parentAliases;
    private final Command command;
    @Nullable
    private final Command parent;
    private final boolean isChild;

    @Getter(AccessLevel.NONE)
    private Map<String, ParameterContext> parameters;

    public MethodCommandExecutorContext(Method method, Class<?> type) {
        final Exceptional<Command> annotated = Reflect.annotation(method, Command.class);
        if (annotated.absent()) throw new IllegalArgumentException("Provided method is not a command handler");
        this.method = method;
        this.type = type;
        this.command = annotated.get();

        final Exceptional<Command> annotation = Reflect.annotation(type, Command.class);
        if (annotation.present()) {
            this.parent = annotation.get();
            this.isChild = true;
        }
        else {
            this.parent = null;
            this.isChild = false;
        }

        this.add(new SimpleCommandDefinitionContext(this.command));

        this.parentAliases = HartshornUtils.emptyList();
        if (this.parent != null) {
            this.parentAliases.addAll(HartshornUtils.asList(this.parent.value()));
        }
        this.parameters = this.parameters();
    }

    private Map<String, ParameterContext> parameters() {
        if (this.parameters == null) {
            this.parameters = HartshornUtils.emptyMap();
            Parameter[] methodParameters = this.method.getParameters();

            for (int i = 0; i < methodParameters.length; i++) {
                Parameter parameter = methodParameters[i];
                this.parameters.put(parameter.getName(), new ParameterContext(parameter, i));
            }
        }
        return this.parameters;
    }

    @Override
    public CommandExecutor executor() {
        return (ctx) -> {
            final Cancellable before = new Before(ctx.source(), ctx).post();
            if (before.cancelled()) {
                final ResourceEntry cancelled = Hartshorn.context().get(CommandResources.class).cancelled();
                ctx.source().send(cancelled);
            }

            final Object instance = Hartshorn.context().get(this.type());
            final List<Object> arguments = this.arguments(ctx);
            try {
                this.method.invoke(instance, arguments.toArray());
                new CommandEvent.After(ctx.source(), ctx).post();
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                Except.handle(e);
            }
        };
    }

    @Override
    public boolean accepts(String command) {
        final CommandDefinitionContext context = this.definition();
        return context.matches(this.strip(command, true));
    }

    @Override
    public String strip(String command, boolean parentOnly) {
        command = this.stripAny(command, this.parentAliases);
        if (!parentOnly) command = this.stripAny(command, this.definition().aliases());
        return command;
    }

    @Override
    public List<String> aliases() {
        List<String> aliases = HartshornUtils.emptyList();
        for (String parentAlias : this.parentAliases()) {
            for (String alias : this.command.value()) {
                aliases.add(parentAlias + ' ' + alias);
            }
        }
        return aliases;
    }

    @Override
    public Class<?> parent() {
        return this.type;
    }

    @Override
    public AnnotatedElement element() {
        return this.method;
    }

    @Override
    public List<String> suggestions(CommandSource source, String command, CommandParser parser) {
        final String stripped = this.strip(command, false);
        final List<CommandElement<?>> elements = this.definition().elements();
        final List<String> tokens = HartshornUtils.asList(stripped.split(" "));
        if (command.endsWith(" ") && !"".equals(tokens.get(tokens.size() - 1))) tokens.add("");

        CommandElement<?> last = null;
        for (CommandElement<?> element : elements) {
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

        if (last == null) return HartshornUtils.emptyList();
        return HartshornUtils.asUnmodifiableList(last.suggestions(source, String.join(" ", tokens)));
    }

    private CommandDefinitionContext definition() {
        final Exceptional<CommandDefinitionContext> definition = this.first(CommandDefinitionContext.class);
        if (definition.absent()) throw new IllegalStateException("Definition context was lost!");
        return definition.get();
    }

    private String stripAny(String command, Iterable<String> aliases) {
        for (String alias : aliases) {
            // Equality is expected when no required arguments are present afterwards
            if (command.equals(alias)) command = "";
            else if (command.startsWith(alias + ' ')) command = command.substring(alias.length() + 1);
        }
        return command;
    }

    private List<Object> arguments(CommandContext context) {
        final List<Object> arguments = HartshornUtils.list(this.parameters().size());
        final Map<String, ParameterContext> parameters = this.parameters();

        for (Entry<String, ParameterContext> entry : parameters.entrySet()) {
            final ParameterContext parameterContext = entry.getValue();
            final int index = parameterContext.index();
            if (parameterContext.is(CommandContext.class)) arguments.set(index, context);
            else {
                @Nullable final Object object = context.get(entry.getKey());
                // Target comparison is done last as this can target either the command source, or a parameter target
                if (object == null && parameterContext.is(Subject.class)) arguments.set(index, context.source());
                else arguments.set(index, object);
            }
        }
        return arguments;
    }
}
