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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.util.Collection;

import lombok.AllArgsConstructor;

/**
 * Simple implementation of a value-based {@link CommandFlag}. Using an underlying
 * {@link CommandElement} to delegate value parsing.
 *
 * @param <T>
 *         The type of the expected value
 */
@AllArgsConstructor
public class CommandFlagElement<T> implements CommandFlag, CommandElement<T> {

    private final CommandElement<T> element;

    @Override
    public boolean optional() {
        return true;
    }

    @Override
    public Exceptional<T> parse(final CommandSource source, final String argument) {
        return this.element.parse(source, argument);
    }

    @Override
    public Collection<String> suggestions(final CommandSource source, final String argument) {
        return this.element.suggestions(source, argument);
    }

    @Override
    public int size() {
        return this.element.size();
    }

    @Override
    public String name() {
        return HartshornUtils.trimWith('-', this.element.name());
    }

    @Override
    public boolean value() {
        return true;
    }
}
