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

package org.dockbox.hartshorn.commands.extension;

import org.dockbox.hartshorn.core.domain.Identifiable;
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
     * @return <code>true</code> if the executor should be modified, or <code>false</code>
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
    default String id(final Identifiable sender, final CommandContext context) {
        final UUID uuid = sender.uniqueId();
        final String alias = context.alias();
        return uuid + "$" + alias;
    }

}
