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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.List;

/**
 * The gateway type to handle command-related functionality.
 */
public interface CommandGateway {

    /**
     * Performs a command based on the given {@link CommandSource} and raw <code>command</code>.
     * The way the provided <code>command</code> is parsed depends on the underlying {@link CommandParser}.
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
     * @param type The type containing {@link org.dockbox.hartshorn.commands.annotations.Command} methods.
     */
    <T> void register(TypeContext<T> type);

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
     * @return The first {@link CommandExecutorContext}, or {@link Exceptional#empty()}
     */
    Exceptional<CommandExecutorContext> get(CommandContext context);

    /**
     * Adds the given {@link CommandExecutorExtension} to any {@link CommandExecutorContext} stored in the
     * gateway.
     *
     * @param extension The extension to add
     */
    void add(CommandExecutorExtension extension);

}
