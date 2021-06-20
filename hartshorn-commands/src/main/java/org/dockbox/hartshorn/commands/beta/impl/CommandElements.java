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
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandElements {

    public static <E extends Enum<E>> CommandElement<E> enumElement(String name, Permission permission, Class<E> type, boolean optional) {
        return new EnumCommandElement<>(name, permission, type, optional);
    }

    private static class EnumCommandElement<E extends Enum<E>> implements CommandElement<E> {

        private final String name;
        private final Permission permission;
        private final Map<String, E> values;
        private final boolean optional;

        public EnumCommandElement(String name, Permission permission, Class<E> type, boolean optional) {
            this.name = name;
            this.permission = permission;
            this.values = Arrays.stream(type.getEnumConstants())
                    .collect(Collectors.toMap(value -> value.name().toLowerCase(),
                            Function.identity(), (value, value2) -> {
                                throw new UnsupportedOperationException(type.getCanonicalName() + " contains more than one enum constant "
                                        + "with the same name, only differing by capitalization, which is unsupported.");
                            }
                    ));
            this.optional = optional;
        }

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
        public Exceptional<E> parse(CommandSource source, String argument) {
            return Exceptional.of(this.values.get(argument.toLowerCase()));
        }

        @Override
        public Collection<String> suggestions(CommandSource source, String argument) {
            return HartshornUtils.asUnmodifiableCollection(this.values.keySet());
        }

        @Override
        public int size() {
            return 1;
        }
    }

}
