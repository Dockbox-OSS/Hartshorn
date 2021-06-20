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

package org.dockbox.hartshorn.commands.beta.impl;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.commands.beta.api.CommandElement;
import org.dockbox.hartshorn.commands.context.ArgumentConverter;
import org.dockbox.hartshorn.commands.source.CommandSource;

import java.util.Collection;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleCommandElement<T> implements CommandElement<T> {

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
    public Exceptional<T> parse(CommandSource source, String argument) {
        return this.converter.convert(source, argument);
    }

    @Override
    public Collection<String> suggestions(CommandSource source, String argument) {
        return this.converter.suggestions(source, argument);
    }

    @Override
    public int size() {
        return this.size;
    }

    public boolean optional() {
        return this.optional;
    }

}
