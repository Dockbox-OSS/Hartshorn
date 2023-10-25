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

package org.dockbox.hartshorn.commands.extension;

import org.dockbox.hartshorn.util.Identifiable;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;

import java.util.UUID;

/**
 * Type to allow for commands to be extended without modifying the underlying
 * {@link org.dockbox.hartshorn.commands.CommandExecutor}.
 */
public interface CommandExecutorExtension {

    /**
     * Modifies the behavior of a command executor based on the given contexts. The returned
     * {@link ExtensionResult} indicates whether the executor should send a message to the
     * player, the message to be sent, and whether the executor should directly continue
     * activating the command.
     *
     * @param context The command context containing the parsed command
     * @param executorContext The executor context
     *
     * @return The result of the extension
     * @see CommandExecutorContext
     */
    ExtensionResult execute(CommandContext context, CommandExecutorContext executorContext);

    /**
     * Gets whether the given executor should be modified.
     *
     * @param context The executor context
     *
     * @return {@code true} if the executor should be modified, or {@code false}
     */
    boolean extend(CommandExecutorContext context);

    /**
     * Gets the unique ID for the given sender and command context.
     *
     * @param sender The identifiable sender, typically a {@link CommandSource}.
     * @param context The command context containing the parsed command
     *
     * @return The unique ID
     */
    default String id(Identifiable sender, CommandContext context) {
        UUID uuid = sender.uniqueId();
        String alias = context.alias();
        return uuid + "$" + alias;
    }

}
