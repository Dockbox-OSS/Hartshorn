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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

/**
 * The default (abstract) implementation for {@link ArgumentConverter argument converters}.
 *
 * @param <T> The type the argument is converted into
 */
public abstract class DefaultArgumentConverter<T> implements ArgumentConverter<T> {

    private final String[] keys;
    @Getter private final TypeContext<T> type;
    private final int size;

    protected DefaultArgumentConverter(final TypeContext<T> type, final String... keys) {
        this(type, 1, keys);
    }

    protected DefaultArgumentConverter(final TypeContext<T> type, final int size, final String... keys) {
        if (0 == keys.length)
            throw new IllegalArgumentException("Cannot create argument converter without at least one key");
        this.keys = keys;
        this.type = type;
        this.size = size;
    }

    public List<String> keys() {
        return Arrays.asList(this.keys);
    }

    @Override
    public int size() {
        return this.size;
    }
}
