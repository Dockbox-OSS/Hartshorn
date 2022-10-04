/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.introspect.annotations.NotPrimitiveException;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class TypeUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = CollectionUtilities.ofEntries(
            Tuple.of(boolean.class, Boolean.class),
            Tuple.of(byte.class, Byte.class),
            Tuple.of(char.class, Character.class),
            Tuple.of(double.class, Double.class),
            Tuple.of(float.class, Float.class),
            Tuple.of(int.class, Integer.class),
            Tuple.of(long.class, Long.class),
            Tuple.of(short.class, Short.class)
    );
    private static final Map<?, Function<String, ?>> PRIMITIVE_FROM_STRING = CollectionUtilities.ofEntries(
            Tuple.of(boolean.class, Boolean::valueOf),
            Tuple.of(byte.class, Byte::valueOf),
            Tuple.of(char.class, s -> s.charAt(0)),
            Tuple.of(double.class, Double::valueOf),
            Tuple.of(float.class, Float::valueOf),
            Tuple.of(int.class, Integer::valueOf),
            Tuple.of(long.class, Long::valueOf),
            Tuple.of(short.class, Short::valueOf)
    );

    public static <T> T toPrimitive(Class<?> type, final String value) throws TypeConversionException, NotPrimitiveException {
        if (type.isEnum()) {
            final String name = String.valueOf(value).toUpperCase();
            //noinspection unchecked,rawtypes
            return (T) Enum.valueOf((Class<? extends Enum>) type, name);
        }
        else {
            if (!type.isPrimitive()) {
                for (final Entry<Class<?>, Class<?>> entry : PRIMITIVE_WRAPPERS.entrySet()) {
                    if (isPrimitiveWrapper(type, entry.getKey())) {
                        type = entry.getKey();
                    }
                }
            }

            if (!type.isPrimitive()) {
                throw new NotPrimitiveException(type);
            }
            else {
                try {
                    final Function<String, ?> converter = PRIMITIVE_FROM_STRING.get(type);
                    return adjustWildcards(converter.apply(value), Object.class);
                }
                catch (final Throwable e) {
                    throw new TypeConversionException(type, value, e);
                }
            }
        }
    }

    private static boolean isPrimitiveWrapper(final Class<?> targetClass, final Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("Expected second argument to be primitive type");
        }
        return PRIMITIVE_WRAPPERS.get(primitive) == targetClass;
    }

    public static <InstanceType, KeyType extends InstanceType, AdjustedType extends KeyType> AdjustedType adjustWildcards(final InstanceType obj, final Class<KeyType> type) {
        if (obj == null) return null;
        if (type.isAssignableFrom(obj.getClass()))
            //noinspection unchecked
            return (AdjustedType) obj;
        throw new IllegalArgumentException("Cannot adjust wildcards for " + obj.getClass().getName() + " to " + type.getName());
    }
}
