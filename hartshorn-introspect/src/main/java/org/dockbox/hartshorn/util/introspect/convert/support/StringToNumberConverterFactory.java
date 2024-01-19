/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util.introspect.convert.support;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

/**
 * Converts a {@link String} to a {@link Number}. Supports all primitive type wrappers, but not
 * primitives themselves. Supports both decimal and hexadecimal numbers.
 *
 * @since 0.5.0
 *
 * @see Integer#parseInt(String)
 * @see Integer#decode(String)
 * @see Long#parseLong(String)
 * @see Long#decode(String)
 * @see Float#parseFloat(String)
 * @see Double#parseDouble(String)
 * @see Short#parseShort(String)
 * @see Short#decode(String)
 * @see Byte#parseByte(String)
 * @see Byte#decode(String)
 * @see #isHexNumber(String)
 *
 * @author Guus Lieben
 */
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {

    @Override
    public <O extends Number> Converter<String, O> create(Class<O> targetType) {
        Converter<String, ? extends Number> converter;
        if (targetType == Integer.class) {
            converter = new StringToNumberConverter<>(Integer::parseInt, Integer::decode);
        }
        else if (targetType == Long.class) {
            converter = new StringToNumberConverter<>(Long::parseLong, Long::decode);
        }
        else if (targetType == Float.class) {
            converter = new StringToNumberConverter<>(Float::parseFloat, input -> Long.decode(input).floatValue());
        }
        else if (targetType == Double.class) {
            converter = new StringToNumberConverter<>(Double::parseDouble, input -> Long.decode(input).doubleValue());
        }
        else if (targetType == Short.class) {
            converter = new StringToNumberConverter<>(Short::parseShort, Short::decode);
        }
        else if (targetType == Byte.class) {
            converter = new StringToNumberConverter<>(Byte::parseByte, Byte::decode);
        }
        else {
            throw new IllegalArgumentException("Unsupported Number type: " + targetType);
        }
        //noinspection unchecked
        return (Converter<String, O>) converter;
    }

    /**
     * A {@link Converter} implementation that converts a {@link String} to a {@link Number}. Supports both decimal and
     * hexadecimal numbers. Uses the provided {@link Function}s to parse and decode the input.
     *
     * @param parseFunction The function to use for parsing decimal numbers
     * @param decodeFunction The function to use for decoding hexadecimal numbers
     * @param <T> The type of the number
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    private record StringToNumberConverter<T extends Number>(
            Function<String, T> parseFunction,
            Function<String, T> decodeFunction
    ) implements Converter<String, T> {

        @Override
        public @Nullable T convert(@Nullable String input) {
            assert input != null;
            try {
                if(isHexNumber(input)) {
                    return this.decodeFunction.apply(input);
                }
                else {
                    return this.parseFunction.apply(input);
                }
            }
            catch(NumberFormatException e) {
                // If primitive, the conversion service will default to zero
                return null;
            }
        }
    }

    /**
     * Returns whether the given {@link String} is a hexadecimal number. A hexadecimal number is prefixed with either
     * {@code 0x} or {@code #}. The prefix may be preceded by a minus sign.
     *
     * @param value the value to check
     * @return {@code true} if the given value is a hexadecimal number, {@code false} otherwise
     */
    public static boolean isHexNumber(String value) {
        int index = value.startsWith("-") ? 1 : 0;
        return value.toLowerCase().startsWith("0x", index) || value.startsWith("#", index);
    }
}
