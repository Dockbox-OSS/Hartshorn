/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.CommandExecutor;
import org.dockbox.hartshorn.commands.CommandParser;
import org.dockbox.hartshorn.commands.CommandResources;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.arguments.CommandParameterLoaderContext;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.events.CommandEvent;
import org.dockbox.hartshorn.commands.events.CommandEvent.Before;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.condition.ProvidedParameterContext;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.events.parents.Cancellable;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple implementation of {@link CommandExecutorContext} targeting {@link Method} based executors.
 */
@Posting({ CommandEvent.Before.class, CommandEvent.After.class })
public class MethodCommandExecutorContext<T> extends DefaultApplicationAwareContext implements CommandExecutorContext {

    private final MethodContext<?, T> method;
    private final Key<T> key;
    private final List<String> parentAliases;
    private final Command command;
    private final boolean isChild;
    private final ParameterLoader<CommandParameterLoaderContext> parameterLoader;

    private Map<String, CommandParameterContext> parameters;

    public MethodCommandExecutorContext(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key) {
        super(context);
        final Result<Command> annotated = method.annotation(Command.class);
        if (annotated.absent()) {
            throw new IllegalArgumentException("Provided method is not a command handler");
        }
        this.method = method;
        this.key = key;
        this.command = annotated.get();

        final Result<Command> annotation = key.type().annotation(Command.class);
        final Command parent;
        if (annotation.present()) {
            parent = annotation.get();
            this.isChild = true;
        }
        else {
            parent = null;
            this.isChild = false;
        }

        this.add(new CommandDefinitionContextImpl(this.applicationContext(), this.command(), this.method()));

        this.parentAliases = new CopyOnWriteArrayList<>();
        if (parent != null) {
            context.log().debug("Parent for executor context of " + method.qualifiedName() + " found, including parent aliases");
            this.parentAliases.addAll(List.of(parent.value()));
        }
        this.parameters = this.parameters();
        this.parameterLoader = context.get(Key.of(ParameterLoader.class, "command_loader"));
    }

    protected MethodContext<?, T> method() {
        return this.method;
    }

    protected Key<T> key() {
        return this.key;
    }

    protected List<String> parentAliases() {
        return this.parentAliases;
    }

    protected Command command() {
        return this.command;
    }

    protected boolean child() {
        return this.isChild;
    }



    protected ParameterLoader<CommandParameterLoaderContext> parameterLoader() {
        return this.parameterLoader;
    }

    public Map<String, CommandParameterContext> parameters() {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
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
        final ConditionMatcher conditionMatcher = applicationContext().get(ConditionMatcher.class);
        return (ctx) -> {
            final Cancellable before = new Before(ctx.source(), ctx).with(this.applicationContext()).post();
            if (before.cancelled()) {
                this.applicationContext().log().debug("Execution cancelled for " + this.method().qualifiedName());
                final Message cancelled = this.applicationContext().get(CommandResources.class).cancelled();
                ctx.source().send(cancelled);
                return;
            }

            final T instance = this.applicationContext().get(this.key());
            final CommandParameterLoaderContext loaderContext = new CommandParameterLoaderContext(this.method(), this.key().type(), null, this.applicationContext(), ctx, this);
            final List<Object> arguments = this.parameterLoader().loadArguments(loaderContext);

            if (conditionMatcher.match(this.method(), ProvidedParameterContext.of(this.method(), arguments))) {
                this.applicationContext().log().debug("Invoking command method %s with %d arguments".formatted(this.method().qualifiedName(), arguments.size()));
                this.method().invoke(instance, arguments.toArray()).caught(error -> this.applicationContext().handle("Encountered unexpected error while performing command executor", error));
                new CommandEvent.After(ctx.source(), ctx).with(this.applicationContext()).post();
            }
            else {
                this.applicationContext().log().debug("Conditions didn't match for " + this.method().qualifiedName());
                final Message cancelled = this.applicationContext().get(CommandResources.class).cancelled();
                ctx.source().send(cancelled);
            }
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
        final List<String> aliases = new ArrayList<>();
        for (final String parentAlias : this.parentAliases()) {
            for (final String alias : this.command().value()) {
                aliases.add(parentAlias + ' ' + alias);
            }
        }
        if (aliases.isEmpty()) {
            aliases.add(this.method().name());
        }
        return List.copyOf(aliases);
    }

    @Override
    public TypeContext<?> parent() {
        return this.key.type();
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
        final List<String> tokens = new ArrayList<>(List.of(stripped.split(" ")));
        if (command.endsWith(" ") && !"".equals(tokens.get(tokens.size() - 1))) tokens.add("");

        CommandElement<?> last = null;
        for (final CommandElement<?> element : elements) {
            int size = element.size();
            if (size == -1) return List.of();

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
            return List.of();
        }
        final Collection<String> suggestions = last.suggestions(source, String.join(" ", tokens));
        this.applicationContext().log().debug("Found " + suggestions.size() + " suggestions");
        return List.copyOf(suggestions);
    }

    private CommandDefinitionContext definition() {
        final Result<CommandDefinitionContext> definition = this.first(CommandDefinitionContext.class);
        if (definition.absent()) throw new DefinitionContextLostException();
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
}
