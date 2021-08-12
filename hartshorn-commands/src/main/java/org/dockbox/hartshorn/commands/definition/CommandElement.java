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

package org.dockbox.hartshorn.commands.definition;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;

/**
 * Represents a single value-carrying command element. This can be either
 * a command argument, or a 'value' flag.
 *
 * @param <T>
 *         The type this element holds
 */
public interface CommandElement<T> extends CommandPartial {

    /**
     * Indicates whether the element is optional. If the element is optional a
     * {@link org.dockbox.hartshorn.commands.CommandParser} may decide to ignore
     * it if no value is provided.
     *
     * @return Whether the element is optional.
     */
    boolean optional();

    /**
     * Converts a given raw argument into type <code>T</code>. The provided
     * {@link CommandSource} may provide additional context to the implementation.
     *
     * @param source
     *         The command source executing a command containing the argument
     * @param argument
     *         The raw argument without the associated key
     *
     * @return The converted object of type <code>T</code>, or {@link Exceptional#empty()}
     */
    Exceptional<T> parse(CommandSource source, String argument);

    /**
     * Gets the suggestions to complete a currently incomplete argument value. Suggestions
     * are complete values.
     * <p>For example, when possible values are: <code>one, two, three</code>, with the input
     * being <code>t</code>, the returned collection will be <code>two, three</code>.
     *
     * @param source
     *         The command source executing a command containing the argument
     * @param argument
     *         The incomplete raw argument
     *
     * @return All suggested values, or {@link HartshornUtils#emptyList()}
     */
    Collection<String> suggestions(CommandSource source, String argument);

    /**
     * Gets the amount of tokens the element expects. A token is a single value separated by
     * a delimiter, typically a single space. For example, if the element expects a first and
     * last name, the size will be 2 (two). If the size is -1 (negative one) the converter is
     * expected to be able to accept all remaining tokens.
     *
     * @return The amount of expected tokens
     */
    int size();

}
