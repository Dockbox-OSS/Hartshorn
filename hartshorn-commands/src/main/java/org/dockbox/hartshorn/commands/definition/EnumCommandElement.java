/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.commands.definition;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class EnumCommandElement<E extends Enum<E>> implements CommandElement<E> {

    private final String name;
    private final Map<String, E> values;
    private final boolean optional;

    EnumCommandElement(final String name, final TypeContext<E> type, final boolean optional) {
        this.name = name;
        this.values = type.enumConstants().stream().collect(Collectors.toMap(value -> value.name().toLowerCase(), Function.identity(), (value, value2) -> {
            throw new UnsupportedOperationException(type.qualifiedName() + " contains more than one enum constant with the same name, only differing by capitalization, which is unsupported.");
        }));
        this.optional = optional;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean optional() {
        return this.optional;
    }

    @Override
    public Exceptional<E> parse(final CommandSource source, final String argument) {
        return Exceptional.of(this.values.get(argument.toLowerCase()));
    }

    @Override
    public Collection<String> suggestions(final CommandSource source, final String argument) {
        return HartshornUtils.asUnmodifiableCollection(this.values.keySet()).stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(argument.toLowerCase(Locale.ROOT)))
                .toList();
    }

    @Override
    public int size() {
        return 1;
    }

    public static <E extends Enum<E>> CommandElement<E> of(final String name, final TypeContext<E> type, final boolean optional) {
        return new EnumCommandElement<>(name, type, optional);
    }
}
