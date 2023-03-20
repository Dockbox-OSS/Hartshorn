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
import org.dockbox.hartshorn.util.option.Option;

/**
 * Parser type capable of parsing a given command into a new {@link CommandContext} based on the
 * source and executing context.
 */
@FunctionalInterface
public interface CommandParser {

    /**
     * Parses the given {@code command} into a new {@link CommandContext} based on the given
     * {@link CommandSource} and executing {@link CommandExecutorContext}.
     *
     * @param command The raw command to parse
     * @param source The {@link CommandSource} executing the command
     * @param context The {@link CommandExecutorContext} executing and handling the command
     *
     * @return The {@link CommandContext} if the command was parsed, or {@link Option#empty()}
     * @throws ParsingException If the command could not be parsed
     */
    Option<CommandContext> parse(String command, CommandSource source, CommandExecutorContext context) throws ParsingException;

}
