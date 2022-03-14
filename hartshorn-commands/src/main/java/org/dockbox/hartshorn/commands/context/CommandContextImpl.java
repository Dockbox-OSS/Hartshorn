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

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.core.CollectionUtilities;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.DefaultContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Collections;
import java.util.List;

/**
 * Simple implementation of {@link CommandContext}.
 */
public class CommandContextImpl extends DefaultContext implements CommandContext {

    private final String command;
    private final List<CommandParameter<?>> args;
    private final List<CommandParameter<?>> flags;
    private final CommandSource source;
    private final ApplicationContext applicationContext;

    public CommandContextImpl(final String command, final List<CommandParameter<?>> args, final List<CommandParameter<?>> flags, final CommandSource source, final ApplicationContext applicationContext) {
        this.command = command;
        this.args = args;
        this.flags = flags;
        this.source = source;
        this.applicationContext = applicationContext;
    }

    public CommandSource source() {
        return this.source;
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public List<CommandParameter<?>> arguments() {
        return Collections.unmodifiableList(this.args);
    }

    @Override
    public <T> T get(final String key) {
        return CollectionUtilities.merge(this.args, this.flags)
                .stream()
                .map(CommandParameter.class::cast)
                .filter(arg -> arg.trimmedKey().equals(key))
                .findFirst()
                .map(arg -> (T) arg.value())
                .orElse(null);
    }

    @Override
    public List<CommandParameter<?>> flags() {
        return Collections.unmodifiableList(this.flags);
    }

    @Override
    public boolean has(final String key) {
        return CollectionUtilities.merge(this.args, this.flags)
                .stream()
                .map(CommandParameter.class::cast)
                .anyMatch(arg -> arg.trimmedKey().equals(key));
    }

    @Override
    public String alias() {
        return this.command.split(" ")[0];
    }

    @Override
    public <T> Exceptional<T> find(final String key) {
        return Exceptional.of(() -> this.get(key));
    }

    @Override
    public <T> Exceptional<CommandParameter<T>> argument(final String key) {
        return Exceptional.of(this.args.stream()
                .filter(arg -> arg.trimmedKey().equals(key))
                .findFirst()
        ).map(arg -> (CommandParameter<T>) arg);
    }

    @Override
    public <T> Exceptional<CommandParameter<T>> flag(final String key) {
        return Exceptional.of(this.flags.stream()
                .filter(flag -> flag.trimmedKey().equals(key))
                .findFirst()
        ).map(flag -> (CommandParameter<T>) flag);
    }

    @Override
    public String command() {
        return this.command;
    }
}
