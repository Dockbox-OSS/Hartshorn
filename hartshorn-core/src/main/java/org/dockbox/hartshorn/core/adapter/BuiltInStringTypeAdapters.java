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

package org.dockbox.hartshorn.core.adapter;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.UUID;

public final class BuiltInStringTypeAdapters {

    public static final StringTypeAdapter<String> STRING = StringTypeAdapterImpl.of(String.class, Exceptional::of);

    public static final StringTypeAdapter<Character> CHARACTER = StringTypeAdapterImpl.of(Character.class, in -> {
        int length = in.length();
        return 1 == length ? Exceptional.of(in.charAt(0)) : Exceptional.empty();
    });

    public static final StringTypeAdapter<Boolean> BOOLEAN = StringTypeAdapterImpl.of(Boolean.class, in -> switch (in) {
        case "yes" -> Exceptional.of(true);
        case "no" -> Exceptional.of(false);
        default -> Exceptional.of(in).map(Boolean::parseBoolean);
    });

    public static final StringTypeAdapter<Double> DOUBLE = StringTypeAdapterImpl.of(Double.class, in -> Exceptional.of(in).map(Double::parseDouble));

    public static final StringTypeAdapter<Float> FLOAT = StringTypeAdapterImpl.of(Float.class, in -> Exceptional.of(in).map(Float::parseFloat));

    public static final StringTypeAdapter<Integer> INTEGER = StringTypeAdapterImpl.of(Integer.class, in -> Exceptional.of(in).map(Integer::parseInt));

    public static final StringTypeAdapter<Long> LONG = StringTypeAdapterImpl.of(Long.class, in -> Exceptional.of(in).map(Long::parseLong));

    public static final StringTypeAdapter<Short> SHORT = StringTypeAdapterImpl.of(Short.class, in -> Exceptional.of(in).map(Short::parseShort));

    public static final StringTypeAdapter<UUID> UNIQUE_ID = StringTypeAdapterImpl.of(UUID.class, in -> Exceptional.of(in).map(UUID::fromString));

    private BuiltInStringTypeAdapters() {}
}
