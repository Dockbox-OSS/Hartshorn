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

import java.util.Map;

import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

/**
 * Converts a {@link String} to a primitive value. Supports all primitive types except {@code void}. Delegates to
 * {@link StringToNumberConverterFactory} for numeric types, and {@link StringToBooleanConverter} and
 * {@link StringToCharacterConverter} for boolean and character types respectively.
 *
 * @see StringToNumberConverterFactory
 * @see StringToBooleanConverter
 * @see StringToCharacterConverter
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class StringToPrimitiveConverterFactory implements ConverterFactory<String, Object>, ConditionalConverter {

    private static final ConverterFactory<String, Number> NUMBER_CONVERTER_FACTORY = new StringToNumberConverterFactory();
    private static final Map<Class<?>, Converter<String, ?>> PRIMITIVE_CONVERTERS = Map.ofEntries(
            Map.entry(boolean.class, new StringToBooleanConverter().andThen(aBoolean -> aBoolean.booleanValue())),
            Map.entry(char.class, new StringToCharacterConverter().andThen(character -> character.charValue())),
            Map.entry(byte.class, NUMBER_CONVERTER_FACTORY.create(Byte.class).andThen(aByte -> aByte.byteValue())),
            Map.entry(double.class, NUMBER_CONVERTER_FACTORY.create(Double.class).andThen(aDouble -> aDouble.doubleValue())),
            Map.entry(float.class, NUMBER_CONVERTER_FACTORY.create(Float.class).andThen(aFloat -> aFloat.floatValue())),
            Map.entry(int.class, NUMBER_CONVERTER_FACTORY.create(Integer.class).andThen(integer -> integer.intValue())),
            Map.entry(long.class, NUMBER_CONVERTER_FACTORY.create(Long.class).andThen(aLong -> aLong.longValue())),
            Map.entry(short.class, NUMBER_CONVERTER_FACTORY.create(Short.class).andThen(aShort -> aShort.shortValue()))
    );

    @SuppressWarnings("unchecked")
    @Override
    public <O> Converter<String, O> create(Class<O> targetType) {
        return (Converter<String, O>) PRIMITIVE_CONVERTERS.get(targetType);
    }

    @Override
    public boolean canConvert(Object source, Class<?> targetType) {
        if (!targetType.isPrimitive()) {
            return false;
        }
        if (PRIMITIVE_CONVERTERS.containsKey(targetType)) {
            return true;
        }
        // Should never encounter this case, but just in case
        throw new IllegalArgumentException("No primitive converter found for primitive type " + targetType.getName());
    }
}
