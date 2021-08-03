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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.i18n.permissions.Permission;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.definition.CommandFlag;
import org.dockbox.hartshorn.di.context.Context;
import org.dockbox.hartshorn.i18n.permissions.PermissionContext;

import java.util.List;

/**
 * Context related to a command definition, this is typically contained in
 * a {@link CommandExecutorContext}.
 */
public interface CommandDefinitionContext extends Context {

    /**
     * Gets all possible aliases for a command.
     * @return All possible aliases.
     */
    List<String> aliases();

    /**
     * Gets the raw argument definition of a command. This is typically a direct representation of
     * {@link Command#arguments()}.
     * @return The raw argument definition.
     */
    String arguments();

    /**
     * Gets the permission required for a command. This typically only carries a raw permission node
     * without addition {@link PermissionContext}. This
     * is typically either a direct representation of {@link Command#permission()} or a generated
     * permission (created by the implementation of this context).
     * @return The required permission.
     */
    Permission permission();

    /**
     * Gets the parent/owner of a command. If no explicit owner exists {@link Void} is returned instead.
     * @return The parent/owner.
     */
    Class<?> parent();

    /**
     * Gets all elements/arguments of a command, excluding flags. The typically represents the parsed
     * elements generated from {@link Command#arguments()}.
     * @return The elements.
     */
    List<CommandElement<?>> elements();

    /**
     * Gets all flags of a command. This typically represents any flags present in {@link Command#arguments()}
     * excluding regular elements/arguments.
     * @return The flags.
     */
    List<CommandFlag> flags();

    /**
     * Gets the definition of a flag, indicating its name and whether it expects a value.
     * @param name The name of the flag.
     * @return The flag definition, or {@link Exceptional#empty()}
     * @see CommandFlag#value()
     */
    Exceptional<CommandFlag> flag(String name);

    /**
     * Checks if a provided raw command matches the contained definition. This typically validates the given
     * arguments, flags, and command alias.
     * @param command The raw command.
     * @return <code>true</code> if the command matches, else <code>false</code>
     */
    boolean matches(String command);
}
