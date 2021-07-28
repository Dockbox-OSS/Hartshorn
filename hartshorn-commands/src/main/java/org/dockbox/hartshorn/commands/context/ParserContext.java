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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.di.context.Context;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Context related to a simple command result. This contains the parsed arguments and flags, and
 * the original command. This should only be used for basic access, more specialized actions should
 * make use of {@link CommandContext} instead.
 */
public interface ParserContext extends Context {

    /**
     * Gets all registered arguments in the form of {@link CommandParameter}. If no arguments are
     * registered, an empty list is returned instead.
     * @return All registered arguments.
     */
    @UnmodifiableView
    List<CommandParameter<?>> arguments();

    /**
     * Gets all registered flags in the form of {@link CommandParameter}. If no flags are registered,
     * an empty list is returned instead.
     * @return All registered flags.
     */
    @UnmodifiableView
    List<CommandParameter<?>> flags();

    /**
     * Gets the alias of a command. Typically a raw command will contain both the alias and additional
     * arguments and flags. The result of this method is only the alias. For example the raw command:
     * <pre><code>
     *     "command argumentA --flagB"
     * </code></pre>
     * Will result in:
     * <pre><code>
     *     "command"
     * </code></pre>
     * @return The command alias.
     */
    String alias();

}
