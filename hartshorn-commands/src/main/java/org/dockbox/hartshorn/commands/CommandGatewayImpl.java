/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.commands;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.ArgumentConverterRegistry;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.context.MethodCommandExecutorContext;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.commands.extension.ExtensionResult;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.util.collections.CopyOnWriteArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of {@link CommandGateway}.
 */
public class CommandGatewayImpl implements CommandGateway {

    private static final Logger LOG = LoggerFactory.getLogger(CommandGatewayImpl.class);

    private final transient MultiMap<String, CommandExecutorContext> contexts = new CopyOnWriteArrayListMultiMap<>();
    private final transient List<CommandExecutorExtension> extensions = new CopyOnWriteArrayList<>();

    private final CommandParser parser;
    private final CommandResources resources;
    private final ApplicationContext context;
    private final ArgumentConverterRegistry converterRegistry;

    public CommandGatewayImpl(
            CommandParser parser,
            CommandResources resources,
            ApplicationContext context,
            ArgumentConverterRegistry converterRegistry
    ) {
        this.parser = parser;
        this.resources = resources;
        this.context = context;
        this.converterRegistry = converterRegistry;
    }

    protected MultiMap<String, CommandExecutorContext> contexts() {
        return this.contexts;
    }

    protected List<CommandExecutorExtension> extensions() {
        return this.extensions;
    }

    @PostConstruct
    public void configureExtensions() {
        if (this.extensions.isEmpty()) {
            ComponentCollection<CommandExecutorExtension> extensions = this.context.get(ComponentKey.collect(CommandExecutorExtension.class));
            for (CommandExecutorExtension extension : extensions) {
                LOG.debug("Adding extension " + extension.getClass().getSimpleName() + " to command gateway");
                this.add(extension);
            }
        }
    }

    @Override
    public void accept(CommandSource source, String command) throws ParsingException {
        Option<CommandExecutorContext> context = this.lookupContext(command);
        if (context.absent()) {
            throw new ParsingException(this.resources.missingHandler(command));
        }
        else {
            Option<CommandContext> commandContext = this.parser.parse(command, source, context.get());
            if (commandContext.present()) {
                this.execute(context.get(), commandContext.get());
            }
            else {
                LOG.warn("Could not parse command for input " + command + " but yielded no exceptions");
            }
        }
    }

    private Option<CommandExecutorContext> lookupContext(String command) {
        String alias = command.split(" ")[0];
        CommandExecutorContext bestContext = null;
        LOG.debug("Looking up executor context for " + command + " in " + this.contexts.size() + " contexts");
        for (CommandExecutorContext context : this.contexts.get(alias)) {
            if (context.accepts(command)) {
                if (bestContext == null) {
                    bestContext = context;
                }
                else {
                    String stripped = context.strip(command, false);
                    // This leaves the arguments without the context's aliases. If the new value is shorter it means more aliases were
                    // stripped, indicating it's providing a deeper level sub-command.
                    if (stripped.length() < bestContext.strip(command, false).length()) {
                        bestContext = context;
                    }
                }
            }
        }
        return Option.of(bestContext);
    }

    protected void execute(CommandExecutorContext context, CommandContext commandContext) {
        for (CommandExecutorExtension extension : this.extensions()) {
            if (extension.extend(context)) {
                ExtensionResult result = extension.execute(commandContext, context);
                if (result.send()) {
                    commandContext.source().send(result.reason());
                }
                if (!result.proceed()) {
                    LOG.debug("Extension " + extension.getClass().getSimpleName() + " rejected direct execution, cancelling command executor.");
                    return;
                }
            }
        }
        context.executor().execute(commandContext);
    }

    @Override
    public void accept(CommandContext context) throws ParsingException {
        Option<CommandExecutorContext> executor = this.get(context);
        executor.peek(executorContext -> this.execute(executorContext, context))
                .orElseThrow(() -> new ParsingException(this.resources.missingExecutor(context.alias(), context.arguments().size())));
    }

    @Override
    public <T> void register(ComponentKey<T> key) {
        TypeView<T> typeView = this.context.environment().introspector().introspect(key.type());
        for (MethodView<T, ?> method : typeView.methods().annotatedWith(Command.class)) {
            this.register(method, key);
        }
    }

    @Override
    public void register(CommandExecutorContext context) {
        Option<CommandDefinitionContext> container = context.firstContext(CommandDefinitionContext.class);
        if (container.absent()) {
            throw new InvalidExecutorException("Executor contexts should contain at least one container context");
        }

        List<String> aliases;
        TypeView<?> typeContext = context.parent();
        Option<Command> annotated = typeContext.annotations().get(Command.class);
        if (!typeContext.isVoid() && annotated.present()) {
            aliases = List.of(annotated.get().value());
        }
        else if (!container.get().aliases().isEmpty()) {
            aliases = container.get().aliases();
        }
        else {
            throw new InvalidExecutorException("Executor should either be declared in command type or container should provide aliases");
        }

        for (String alias : aliases) {
            this.contexts().put(alias, context);
        }
        this.context.addContext(context);
    }

    @Override
    public List<String> suggestions(CommandSource source, String command) {
        Option<CommandExecutorContext> context = this.lookupContext(command);
        List<String> suggestions = new ArrayList<>();

        if (context.present()) {
            suggestions.addAll(context.get().suggestions(source, command, this.parser));
        }

        String alias = command.split(" ")[0];
        Collection<CommandExecutorContext> contexts = this.contexts().get(alias);
        for (CommandExecutorContext executorContext : contexts) {
            for (String contextAlias : executorContext.aliases()) {
                if (contextAlias.startsWith(command)) {
                    String stripped = contextAlias.replaceFirst(alias + " ", "");
                    if (!stripped.isEmpty()) {
                        suggestions.add(stripped);
                    }
                }
            }
        }

        return Collections.unmodifiableList(suggestions);
    }

    @Override
    public Option<CommandExecutorContext> get(CommandContext context) {
        for (CommandExecutorContext executorContext : this.contexts().get(context.alias())) {
            if (executorContext.accepts(context.command())) {
                return Option.of(executorContext);
            }
        }
        return Option.empty();
    }

    @Override
    public void add(CommandExecutorExtension extension) {
        this.extensions.add(extension);
    }

    private <T> void register(MethodView<T, ?> method, ComponentKey<T> key) {
        this.register(new MethodCommandExecutorContext<>(this.context, this.converterRegistry, method, key));
    }
}
