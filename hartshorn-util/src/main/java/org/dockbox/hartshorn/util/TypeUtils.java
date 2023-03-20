/*
 * Copyright 2019-2023 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

public class TypeUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = Map.ofEntries(
            Map.entry(boolean.class, Boolean.class),
            Map.entry(byte.class, Byte.class),
            Map.entry(char.class, Character.class),
            Map.entry(double.class, Double.class),
            Map.entry(float.class, Float.class),
            Map.entry(int.class, Integer.class),
            Map.entry(long.class, Long.class),
            Map.entry(short.class, Short.class)
    );

    private static final Map<?, Function<String, ?>> PRIMITIVE_FROM_STRING = Map.ofEntries(
            Map.entry(boolean.class, Boolean::valueOf),
            Map.entry(byte.class, Byte::valueOf),
            Map.entry(char.class, s -> s.charAt(0)),
            Map.entry(double.class, Double::valueOf),
            Map.entry(float.class, Float::valueOf),
            Map.entry(int.class, Integer::valueOf),
            Map.entry(long.class, Long::valueOf),
            Map.entry(short.class, Short::valueOf)
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

    public static <A extends Annotation> A annotation(final Class<A> annotationType) {
        return annotation(annotationType, Collections.emptyMap());
    }

    public static <A extends Annotation> A annotation(final Class<A> annotationType, final Map<String, Object> values) {
        final Object instance = Proxy.newProxyInstance(annotationType.getClassLoader(),
                new Class[]{ annotationType },
                new MapBackedAnnotationInvocationHandler(annotationType, values == null ? Collections.emptyMap() : values));
        return adjustWildcards(instance, annotationType);
    }

    public static Stream<Integer> stream(final int[] array) {
        return Arrays.stream(array).boxed();
    }

    public static Stream<Long> stream(final long[] array) {
        return Arrays.stream(array).boxed();
    }

    public static Stream<Double> stream(final double[] array) {
        return Arrays.stream(array).boxed();
    }

    public static Stream<Float> stream(final float[] array) {
        final Float[] boxed = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    public static Stream<Short> stream(final short[] array) {
        final Short[] boxed = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    public static Stream<Byte> stream(final byte[] array) {
        final Byte[] boxed = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    public static Stream<Character> stream(final char[] array) {
        final Character[] boxed = new Character[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    public static Stream<Boolean> stream(final boolean[] array) {
        final Boolean[] boxed = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }
}
