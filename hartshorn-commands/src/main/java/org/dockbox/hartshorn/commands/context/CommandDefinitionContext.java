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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.definition.CommandFlag;
import org.dockbox.hartshorn.core.context.Context;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.util.List;

/**
 * Context related to a command definition, this is typically contained in
 * a {@link CommandExecutorContext}.
 */
public interface CommandDefinitionContext extends Context {

    /**
     * Gets all possible aliases for a command.
     *
     * @return All possible aliases.
     */
    List<String> aliases();

    /**
     * Gets the raw argument definition of a command. This is typically a direct representation of
     * {@link Command#arguments()}.
     *
     * @return The raw argument definition.
     */
    String arguments();

    /**
     * Gets the parent/owner of a command. If no explicit owner exists {@link Void} is returned instead.
     *
     * @return The parent/owner.
     */
    TypeContext<?> parent();

    /**
     * Gets all elements/arguments of a command, excluding flags. The typically represents the parsed
     * elements generated from {@link Command#arguments()}.
     *
     * @return The elements.
     */
    List<CommandElement<?>> elements();

    /**
     * Gets all flags of a command. This typically represents any flags present in {@link Command#arguments()}
     * excluding regular elements/arguments.
     *
     * @return The flags.
     */
    List<CommandFlag> flags();

    /**
     * Gets the definition of a flag, indicating its name and whether it expects a value.
     *
     * @param name The name of the flag.
     *
     * @return The flag definition, or {@link Exceptional#empty()}
     * @see CommandFlag#value()
     */
    Exceptional<CommandFlag> flag(String name);

    /**
     * Checks if a provided raw command matches the contained definition. This typically validates the given
     * arguments, flags, and command alias.
     *
     * @param command The raw command.
     *
     * @return <code>true</code> if the command matches, else <code>false</code>
     */
    boolean matches(String command);
}
