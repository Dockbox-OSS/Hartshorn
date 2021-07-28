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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;

/**
 * Parser type capable of parsing a given command into a new {@link CommandContext} based on the
 * source and executing context.
 */
public interface CommandParser {

    /**
     * Parses the given <code>command</code> into a new {@link CommandContext} based on the given
     * {@link CommandSource} and executing {@link CommandExecutorContext}.
     * @param command The raw command to parse
     * @param source The {@link CommandSource} executing the command
     * @param context The {@link CommandExecutorContext} executing and handling the command
     * @return The {@link CommandContext} if the command was parsed, or {@link Exceptional#empty()}
     * @throws ParsingException If the command could not be parsed
     */
    Exceptional<CommandContext> parse(String command, CommandSource source, CommandExecutorContext context) throws ParsingException;

}
