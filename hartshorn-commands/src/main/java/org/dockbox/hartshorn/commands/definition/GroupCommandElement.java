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
import org.dockbox.hartshorn.i18n.permissions.Permission;

import java.util.Collection;
import java.util.List;

/**
 * Represents a group of {@link CommandElement command elements}. This is typically used when
 * a single argument is only valid in combination with another argument.
 */
public class GroupCommandElement implements CommandElement<List<CommandElement<?>>> {

    private final List<CommandElement<?>> elements;
    private final String name;
    private final boolean optional;
    private final int size;

    public GroupCommandElement(final List<CommandElement<?>> elements, final boolean optional) {
        this.elements = elements;
        final List<String> names = elements.stream().map(CommandElement::name).toList();
        this.name = "group: " + String.join(", ", names);
        this.size = elements.stream().mapToInt(CommandElement::size).sum();
        this.optional = optional;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Exceptional<Permission> permission() {
        return Exceptional.empty();
    }

    @Override
    public boolean optional() {
        return this.optional;
    }

    @Override
    public Exceptional<List<CommandElement<?>>> parse(final CommandSource source, final String argument) {
        return Exceptional.of(this.elements);
    }

    @Override
    public Collection<String> suggestions(final CommandSource source, final String argument) {
        throw new UnsupportedOperationException("Collecting suggestions from element groups is not supported, target singular elements instead.");
    }

    @Override
    public int size() {
        return this.size;
    }
}
