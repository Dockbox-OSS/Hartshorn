/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.properties.value;

import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.properties.value.support.ConverterValuePropertyParser;
import org.dockbox.hartshorn.properties.value.support.GenericConverterValuePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToArrayConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToCharacterConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;

/**
 * A collection of standard {@link ValuePropertyParser} instances for common types.
 */
public final class StandardValuePropertyParsers {

    private StandardValuePropertyParsers() {
        // Static access only
    }

    /**
     * A {@link ValuePropertyParser} for parsing boolean values from a {@link ValueProperty}.
     *
     * @see StringToBooleanConverter
     */
    public static final ValuePropertyParser<Boolean> BOOLEAN =
        new ConverterValuePropertyParser<>(new StringToBooleanConverter());

    /**
     * A {@link ValuePropertyParser} for parsing integer values from a {@link ValueProperty}.
     *
     * @see StringToNumberConverterFactory
     */
    public static final ValuePropertyParser<Integer> INTEGER =
        new ConverterValuePropertyParser<>(
            new StringToNumberConverterFactory().create(Integer.class)
        );

    /**
     * A {@link ValuePropertyParser} for parsing long values from a {@link ValueProperty}.
     *
     * @see StringToNumberConverterFactory
     */
    public static final ValuePropertyParser<Long> LONG =
        new ConverterValuePropertyParser<>(
            new StringToNumberConverterFactory().create(Long.class)
        );

    /**
     * A {@link ValuePropertyParser} for parsing double values from a {@link ValueProperty}.
     *
     * @see StringToNumberConverterFactory
     */
    public static final ValuePropertyParser<Double> DOUBLE =
        new ConverterValuePropertyParser<>(
            new StringToNumberConverterFactory().create(Double.class)
        );

    /**
     * A {@link ValuePropertyParser} for parsing float values from a {@link ValueProperty}.
     *
     * @see StringToNumberConverterFactory
     */
    public static final ValuePropertyParser<Float> FLOAT =
        new ConverterValuePropertyParser<>(
            new StringToNumberConverterFactory().create(Float.class)
        );

    /**
     * A {@link ValuePropertyParser} for parsing string values from a {@link ValueProperty}. Uses
     * the direct string representation of the value.
     *
     * @see ValueProperty#value()
     */
    public static final ValuePropertyParser<String> STRING = ValueProperty::value;

    /**
     * A {@link ValuePropertyParser} for parsing character values from a {@link ValueProperty}.
     *
     * @see StringToCharacterConverter
     */
    public static final ValuePropertyParser<Character> CHARACTER =
        new ConverterValuePropertyParser<>(new StringToCharacterConverter());

    /**
     * A {@link ValuePropertyParser} for parsing short values from a {@link ValueProperty}.
     *
     * @see StringToNumberConverterFactory
     */
    public static final ValuePropertyParser<Short> SHORT =
        new ConverterValuePropertyParser<>(
            new StringToNumberConverterFactory().create(Short.class)
        );

    /**
     * A {@link ValuePropertyParser} for parsing byte values from a {@link ValueProperty}.
     *
     * @see StringToNumberConverterFactory
     */
    public static final ValuePropertyParser<Byte> BYTE =
        new ConverterValuePropertyParser<>(new StringToNumberConverterFactory().create(Byte.class));

    /**
     * A {@link ValuePropertyParser} for parsing arrays of strings from a {@link ValueProperty}. The
     * string representation of the property is expected to be a comma-separated list of values.
     *
     * @see StringToArrayConverter
     */
    public static final ValuePropertyParser<String[]> STRING_LIST =
        new GenericConverterValuePropertyParser<>(new StringToArrayConverter(), String[].class);
}
