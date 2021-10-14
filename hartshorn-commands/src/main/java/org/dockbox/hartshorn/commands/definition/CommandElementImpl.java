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
import org.dockbox.hartshorn.i18n.permissions.Permission;

import java.util.Collection;

import lombok.AllArgsConstructor;

/**
 * Simple implementation of {@link CommandElement}.
 *
 * @param <T>
 */
@AllArgsConstructor
public class CommandElementImpl<T> implements CommandElement<T> {

    private final ArgumentConverter<T> converter;
    private final String name;
    private final Permission permission;
    private final boolean optional;
    private final int size;

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Exceptional<Permission> permission() {
        return Exceptional.of(this.permission);
    }

    @Override
    public boolean optional() {
        return this.optional;
    }

    @Override
    public Exceptional<T> parse(final CommandSource source, final String argument) {
        return this.converter.convert(source, argument);
    }

    @Override
    public Collection<String> suggestions(final CommandSource source, final String argument) {
        return this.converter.suggestions(source, argument);
    }

    @Override
    public int size() {
        return this.size;
    }

}