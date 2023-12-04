/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

/**
 * The gateway type to handle command-related functionality.
 */
public interface CommandGateway {

    /**
     * Performs a command based on the given {@link CommandSource} and raw {@code command}.
     * The way the provided {@code command} is parsed depends on the underlying {@link CommandParser}.
     *
     * @param source The {@link CommandSource} performing the command
     * @param command The raw command
     *
     * @throws ParsingException If the provided command could not be parsed or no associated {@link CommandExecutor} could be found
     */
    void accept(CommandSource source, String command) throws ParsingException;

    /**
     * Performs a command based on the given {@link CommandContext}.
     *
     * @param context The parsed {@link CommandContext}
     *
     * @throws ParsingException If the provided context has no associated {@link CommandExecutor}
     */
    void accept(CommandContext context) throws ParsingException;

    /**
     * Registers any methods annotated with {@link org.dockbox.hartshorn.commands.annotations.Command}
     * in the provided {@link Class type} as {@link CommandExecutor executors} capable of handling
     * the commands.
     *
     * @param <T> The type of the class
     * @param key The key containing {@link org.dockbox.hartshorn.commands.annotations.Command} methods.
     */
    <T> void register(ComponentKey<T> key);

    /**
     * Registers the given {@link CommandExecutorContext} to handle the associated command(s).
     *
     * @param context The context
     */
    void register(CommandExecutorContext context);

    /**
     * Gets all possible suggestions for the given raw incomplete command. The suggestions are obtained from
     * the {@link org.dockbox.hartshorn.commands.definition.CommandElement} associated with the last argument
     * in the command.
     *
     * @param source The {@link CommandSource} executing the command
     * @param command The raw command
     *
     * @return The suggestions for the last argument, or empty {@link List}
     */
    List<String> suggestions(CommandSource source, String command);

    /**
     * Gets the (first) {@link CommandExecutorContext} which accepts the given {@link CommandContext}.
     *
     * @param context The context to apply
     *
     * @return The first {@link CommandExecutorContext}, or {@link Option#empty()}
     */
    Option<CommandExecutorContext> get(CommandContext context);

    /**
     * Adds the given {@link CommandExecutorExtension} to any {@link CommandExecutorContext} stored in the
     * gateway.
     *
     * @param extension The extension to add
     */
    void add(CommandExecutorExtension extension);

}
