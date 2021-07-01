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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.commands.exceptions.ConstraintException;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("unused")
public final class ArgumentConverterRegistry {

    private static final transient Collection<ArgumentConverter<?>> CONVERTERS = HartshornUtils.emptyConcurrentList();

    private ArgumentConverterRegistry() {}

    public static boolean hasConverter(String key) {
        return getOptionalConverter(key).present();
    }

    public static boolean hasConverter(Class<?> type) {
        return getOptionalConverter(type).present();
    }
    
    public static Exceptional<ArgumentConverter<?>> getOptionalConverter(String key) {
        Optional<ArgumentConverter<?>> optional = CONVERTERS.stream().filter(converter -> converter.getKeys().stream()
                .map(String::toLowerCase)
                .toList()
                .contains(key.toLowerCase())
        ).findFirst();
        if (optional.isPresent()) return Exceptional.of(optional);
        else return Exceptional.of(new RuntimeException("No converter present"));
    }

    private static <T> Exceptional<ArgumentConverter<T>> getOptionalConverter(Class<T> type) {
        //noinspection unchecked
        return Exceptional.of(CONVERTERS.stream()
                .filter(converter -> Reflect.assignableFrom(converter.getType(), type))
                .map(converter -> (ArgumentConverter<T>) converter)
                .findFirst());
    }

    public static ArgumentConverter<?> getConverter(String key) {
        return getOptionalConverter(key).rethrow().orNull();
    }

    public static <T> ArgumentConverter<T> getConverter(Class<T> type) {
        return getOptionalConverter(type).orNull();
    }

    public static void registerConverter(ArgumentConverter<?> converter) {
        for (String key : converter.getKeys()) {
            for (ArgumentConverter<?> existingConverter : CONVERTERS) {
                if (existingConverter.getKeys().contains(key)) {
                    throw new ConstraintException("Duplicate argument key '" + key + "' found while registering converter");
                }
            }
        }
        CONVERTERS.add(converter);
    }
}
