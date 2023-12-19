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
            Map.entry(boolean.class, input -> {
                // Boolean.valueOf only checks if the input equals 'true', and otherwise
                // defaults to 'false'. We want to be more strict.
                if ("true".equals(input)) {
                    return true;
                }
                else if ("false".equals(input)) {
                    return false;
                }
                else {
                    throw new TypeConversionException("Invalid boolean value: " + input);
                }
            }),
            Map.entry(byte.class, Byte::valueOf),
            Map.entry(char.class, input -> {
                if (input.length() == 1) {
                    return input.charAt(0);
                }
                else {
                    throw new TypeConversionException("Invalid char value: " + input + " (length != 1)");
                }
            }),
            Map.entry(double.class, Double::valueOf),
            Map.entry(float.class, Float::valueOf),
            Map.entry(int.class, Integer::valueOf),
            Map.entry(long.class, Long::valueOf),
            Map.entry(short.class, Short::valueOf)
    );

    public static <T> T toPrimitive(Class<?> type, String value) throws TypeConversionException, NotPrimitiveException {
        if (type.isEnum()) {
            String name = String.valueOf(value).toUpperCase();
            try {
                //noinspection unchecked,rawtypes
                return (T) Enum.valueOf((Class<? extends Enum>) type, name);
            }
            catch (IllegalArgumentException e) {
                throw new TypeConversionException("No enum constant " + type.getName() + "." + name);
            }
        }
        else {
            if (!type.isPrimitive()) {
                for (Entry<Class<?>, Class<?>> entry : PRIMITIVE_WRAPPERS.entrySet()) {
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
                    Function<String, ?> converter = PRIMITIVE_FROM_STRING.get(type);
                    T result = adjustWildcards(converter.apply(value), Object.class);
                    if (result == null) {
                        throw new TypeConversionException(type, value);
                    }
                    return result;
                }
                catch (Throwable e) {
                    throw new TypeConversionException(type, value, e);
                }
            }
        }
    }

    public static boolean isPrimitiveWrapper(Class<?> targetClass, Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("Expected second argument to be primitive type");
        }
        return PRIMITIVE_WRAPPERS.get(primitive) == targetClass;
    }

    public static <InstanceType extends KeyType, KeyType, AdjustedType extends KeyType> AdjustedType adjustWildcards(InstanceType obj, Class<KeyType> type) {
        if (obj == null) {
            return null;
        }
        if (type.isAssignableFrom(obj.getClass())) {
            //noinspection unchecked
            return (AdjustedType) obj;
        }
        throw new IllegalArgumentException("Cannot adjust wildcards for " + obj.getClass().getName() + " to " + type.getName());
    }

    public static <A extends Annotation> A annotation(Class<A> annotationType) {
        return annotation(annotationType, Collections.emptyMap());
    }

    public static <A extends Annotation> A annotation(Class<A> annotationType, Map<String, Object> values) {
        Object instance = Proxy.newProxyInstance(annotationType.getClassLoader(),
                new Class[]{ annotationType },
                new MapBackedAnnotationInvocationHandler(annotationType, values == null ? Collections.emptyMap() : values));
        return annotationType.cast(instance);
    }

    public static Stream<Integer> stream(int[] array) {
        return Arrays.stream(array).boxed();
    }

    public static Stream<Long> stream(long[] array) {
        return Arrays.stream(array).boxed();
    }

    public static Stream<Double> stream(double[] array) {
        return Arrays.stream(array).boxed();
    }

    public static Stream<Float> stream(float[] array) {
        Float[] boxed = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    public static Stream<Short> stream(short[] array) {
        Short[] boxed = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    public static Stream<Byte> stream(byte[] array) {
        Byte[] boxed = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    public static Stream<Character> stream(char[] array) {
        Character[] boxed = new Character[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    public static Stream<Boolean> stream(boolean[] array) {
        Boolean[] boxed = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    public static boolean isAssignable(Class<?> source, Class<?> target) {
        if (target.isAssignableFrom(source)) {
            return true;
        }
        if (target.isPrimitive() && TypeUtils.isPrimitiveWrapper(source, target)) {
            return true;
        }
        return source.isPrimitive() && TypeUtils.isPrimitiveWrapper(target, source);
    }

    public static <T> Class<T> getClass(T instance) {
        //noinspection unchecked
        return (Class<T>) instance.getClass();
    }
}
