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

package org.dockbox.hartshorn.commands.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.CommandExecutor;
import org.dockbox.hartshorn.commands.CommandParser;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.arguments.CommandParameterLoader;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of {@link CommandExecutorContext} targeting {@link MethodView} based executors.
 */
public class MethodCommandExecutorContext<T> extends DefaultApplicationAwareContext implements CommandExecutorContext {

    private static final Logger LOG = LoggerFactory.getLogger(MethodCommandExecutorContext.class);

    private final MethodView<T, ?> method;
    private final TypeView<T> type;
    private final ComponentKey<T> key;
    private final List<String> parentAliases;
    private final Command command;
    private final boolean isChild;
    private final ParameterLoader parameterLoader;

    private Map<String, CommandParameterContext> parameters;

    public MethodCommandExecutorContext(
            ApplicationContext context,
            ArgumentConverterRegistry converterRegistry,
            MethodView<T, ?> method,
            ComponentKey<T> key
    ) {
        super(context);
        Option<Command> annotated = method.annotations().get(Command.class);
        if (annotated.absent()) {
            throw new IllegalArgumentException("Provided method is not a command handler");
        }
        this.method = method;
        this.key = key;
        this.command = annotated.get();
        this.type = context.environment().introspector().introspect(key.type());

        Option<Command> annotation = this.type.annotations().get(Command.class);
        Command parent;
        if (annotation.present()) {
            parent = annotation.get();
            this.isChild = true;
        }
        else {
            parent = null;
            this.isChild = false;
        }

        this.addContext(new CommandDefinitionContextImpl(this.applicationContext(), converterRegistry, this.command(), this.method()));

        this.parentAliases = new CopyOnWriteArrayList<>();
        if (parent != null) {
            LOG.debug("Parent for executor context of " + method.qualifiedName() + " found, including parent aliases");
            this.parentAliases.addAll(List.of(parent.value()));
        }
        this.parameters = this.parameters();
        this.parameterLoader = new CommandParameterLoader();
    }

    protected MethodView<T, ?> method() {
        return this.method;
    }

    protected ComponentKey<T> key() {
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

    protected ParameterLoader parameterLoader() {
        return this.parameterLoader;
    }

    public Map<String, CommandParameterContext> parameters() {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
            List<ParameterView<?>> parameters = this.method().parameters().all();
            for (int i = 0; i < parameters.size(); i++) {
                ParameterView<?> parameter = parameters.get(i);
                this.parameters.put(parameter.name(), new CommandParameterContext(parameter, i));
            }

        }
        return this.parameters;
    }

    @Override
    public CommandExecutor executor() {
        ConditionMatcher conditionMatcher = this.applicationContext().get(ConditionMatcher.class);
        return new MethodCommandExecutor<>(conditionMatcher, this);
    }

    @Override
    public boolean accepts(String command) {
        CommandDefinitionContext context = this.definition();
        return context.matches(this.strip(command, true));
    }

    @Override
    public String strip(String command, boolean parentOnly) {
        command = this.stripAny(command, this.parentAliases);
        if (!parentOnly) {
            command = this.stripAny(command, this.definition().aliases());
        }
        return command;
    }

    @Override
    public List<String> aliases() {
        List<String> aliases = new ArrayList<>();
        for (String parentAlias : this.parentAliases()) {
            for (String alias : this.command().value()) {
                aliases.add(parentAlias + ' ' + alias);
            }
        }
        if (aliases.isEmpty()) {
            aliases.add(this.method().name());
        }
        return List.copyOf(aliases);
    }

    @Override
    public TypeView<?> parent() {
        return this.type;
    }

    @Override
    public AnnotatedElementView element() {
        return this.method();
    }

    @Override
    public List<String> suggestions(CommandSource source, String command, CommandParser parser) {
        String stripped = this.strip(command, false);
        LOG.debug("Collecting suggestions for stripped input %s (was %s)".formatted(stripped, command));

        List<CommandElement<?>> elements = this.definition().elements();
        List<String> tokens = new ArrayList<>(List.of(stripped.split(" ")));
        if (command.endsWith(" ") && !"".equals(CollectionUtilities.last(tokens))) {
            tokens.add("");
        }

        CommandElement<?> last = null;
        for (CommandElement<?> element : elements) {
            int size = element.size();
            if (size == -1) {
                return List.of();
            }

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
            LOG.debug("Could not locate last command element to collect suggestions");
            return List.of();
        }
        Collection<String> suggestions = last.suggestions(source, String.join(" ", tokens));
        LOG.debug("Found " + suggestions.size() + " suggestions");
        return List.copyOf(suggestions);
    }

    private CommandDefinitionContext definition() {
        Option<CommandDefinitionContext> definition = this.firstContext(CommandDefinitionContext.class);
        if (definition.absent()) {
            throw new DefinitionContextLostException();
        }
        return definition.get();
    }

    private String stripAny(String command, Iterable<String> aliases) {
        for (String alias : aliases) {
            // Equality is expected when no required arguments are present afterwards
            if (command.equals(alias)) {
                command = "";
            }
            else if (command.startsWith(alias + ' ')) {
                command = command.substring(alias.length() + 1);
            }
        }
        return command;
    }
}
