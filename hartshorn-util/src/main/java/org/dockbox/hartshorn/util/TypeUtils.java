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

package org.dockbox.hartshorn.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Utility class for functionalities related to types. Within the context of this class, types can either
 * be primitives which require (un)boxing, or {@link Class} instances.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
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

    /**
     * Converts a string value to a primitive type. If the type is an enum, the value is converted to an enum constant.
     * If the given type is not a primitive, but a primitive wrapper, the value is converted to the primitive type.
     *
     * <p><b>Note</b>: This utility method is kept for backwards compatibility. It is recommended to use the {@code ConversionService}
     * in {@code org.dockbox.hartshorn.util.introspect.convert} instead, as it provides a more robust and extensible way of
     * converting values.
     *
     * @param type The type to convert to
     * @param value The value to convert
     * @param <T> The type to convert to
     *
     * @return The converted value
     *
     * @throws TypeConversionException If the value cannot be converted to the given type
     * @throws NotPrimitiveException If the given type is not a primitive or primitive wrapper
     *
     * @deprecated Use {@code ConversionService} instead. This method will not be removed in a future release, but is no
     *             longer recommended for use.
     */
    @Deprecated(since = "0.6.0", forRemoval = false)
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

    /**
     * Returns whether the given class is a primitive wrapper for the given primitive type. If the given type is not a
     * primitive, an {@link IllegalArgumentException} is thrown.
     *
     * @param targetClass The class to check
     * @param primitive The primitive type to check against
     * @return Whether the given class is a primitive wrapper for the given primitive type
     */
    public static boolean isPrimitiveWrapper(Class<?> targetClass, Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("Expected second argument to be primitive type");
        }
        return PRIMITIVE_WRAPPERS.get(primitive) == targetClass;
    }

    /**
     * Allows adjusting wildcard types, ensuring compatibility between object instances and a specified key type. This method is
     * particularly useful when dealing with scenarios where the generic type information is not precisely known at compile time,
     * but is not required to be known at runtime (either due to type erasure, or due to the use of wildcards).
     *
     * <p>It should be noted that this method does not perform any type conversion. It merely ensures that the given object is
     * compatible with the given type. If the given object is not compatible with the given type, an {@link IllegalArgumentException}
     * is thrown. Compatibility is not ensured for type parameters, but only for the raw type.
     *
     * <p>Due to type parameter constraints, it should never be possible to invoke this method with a raw type that is not assignable
     * to the given type. However, this method will throw an {@link IllegalArgumentException} if this is the case.
     *
     * <p>Taking the following example type parameters for this method:
     * <ul>
     *     <li>{@link InstanceType} - List&lt;?&gt;</li>
     *     <li>{@link KeyType} - List</li>
     *     <li>{@link AdjustedType} - List&lt;String&gt;</li>
     * </ul>
     *
     * <p>When invoking this method with an instance of any {@link java.util.List}, the method will return the same instance, as
     * {@link java.util.List} is assignable to {@link java.util.List}&gt;?&lt;. However, as the instance could be {@code List<Integer>}
     * just as well as {@code List<String>}, caution should be taken before using this method.
     *
     * <p>Proper usage of this method will result in a type-safe cast. For example, when invoking this method with an instance of
     * {@code List<String>}, and the {@link AdjustedType} being {@code List<?>}.
     *
     * @param obj The object to adjust
     * @param type The type to adjust to
     * @param <InstanceType> The type of the object to adjust
     * @param <KeyType> The type to adjust to
     * @param <AdjustedType> The adjusted type
     *
     * @return The adjusted object
     */
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

    /**
     * Creates a new instance of the given annotation type. The returned instance is a proxy that implements the given annotation
     * type. Note that this method assumes the given annotation type only has properties with default values, or no properties at
     * all. If this is not the case for the given annotation type, it is recommended to use {@link #annotation(Class, Map)} instead.
     *
     * @param annotationType The annotation type to create an instance of
     * @param <A> The annotation type
     *
     * @return A new instance of the given annotation type
     *
     * @see MapBackedAnnotationInvocationHandler
     * @see #annotation(Class, Map)
     */
    public static <A extends Annotation> A annotation(Class<A> annotationType) {
        return annotation(annotationType, Collections.emptyMap());
    }

    /**
     * Creates a new instance of the given annotation type. The returned instance is a proxy that implements the given annotation
     * type. The given map is used to populate the properties of the annotation instance. If the given map does not contain a value
     * for a property, the default value of that property is used, which is either the value specified in the annotation definition,
     * or {@code null} if no value is specified.
     *
     * @param annotationType The annotation type to create an instance of
     * @param values The values to populate the annotation instance with
     * @param <A> The annotation type
     *
     * @return A new instance of the given annotation type
     *
     * @see MapBackedAnnotationInvocationHandler
     */
    public static <A extends Annotation> A annotation(Class<A> annotationType, Map<String, Object> values) {
        Object instance = Proxy.newProxyInstance(annotationType.getClassLoader(),
                new Class[]{ annotationType },
                new MapBackedAnnotationInvocationHandler(annotationType, values == null ? Collections.emptyMap() : values));
        return annotationType.cast(instance);
    }

    /**
     * Returns a stream of boxed integers from the given array.
     *
     * @param array The array of primitive ints to stream
     * @return A stream of boxed integers
     */
    public static Stream<Integer> stream(int[] array) {
        return Arrays.stream(array).boxed();
    }

    /**
     * Returns a stream of boxed longs from the given array.
     *
     * @param array The array of primitive longs to stream
     * @return A stream of boxed longs
     */
    public static Stream<Long> stream(long[] array) {
        return Arrays.stream(array).boxed();
    }

    /**
     * Returns a stream of boxed doubles from the given array.
     *
     * @param array The array of primitive doubles to stream
     * @return A stream of boxed doubles
     */
    public static Stream<Double> stream(double[] array) {
        return Arrays.stream(array).boxed();
    }

    /**
     * Returns a stream of boxed floats from the given array.
     *
     * @param array The array of primitive floats to stream
     * @return A stream of boxed floats
     */
    public static Stream<Float> stream(float[] array) {
        Float[] boxed = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    /**
     * Returns a stream of boxed shorts from the given array.
     *
     * @param array The array of primitive shorts to stream
     * @return A stream of boxed shorts
     */
    public static Stream<Short> stream(short[] array) {
        Short[] boxed = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    /**
     * Returns a stream of boxed bytes from the given array.
     *
     * @param array The array of primitive bytes to stream
     * @return A stream of boxed bytes
     */
    public static Stream<Byte> stream(byte[] array) {
        Byte[] boxed = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    /**
     * Returns a stream of boxed characters from the given array.
     *
     * @param array The array of primitive chars to stream
     * @return A stream of boxed characters
     */
    public static Stream<Character> stream(char[] array) {
        Character[] boxed = new Character[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    /**
     * Returns a stream of boxed booleans from the given array.
     *
     * @param array The array of primitive booleans to stream
     * @return A stream of boxed booleans
     */
    public static Stream<Boolean> stream(boolean[] array) {
        Boolean[] boxed = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return Arrays.stream(boxed);
    }

    /**
     * Indicates whether the given source type is assignable to the given target type. This method is similar to
     * {@link Class#isAssignableFrom(Class)}, but also takes primitive types into account. If the given types are
     * not directly assignable, if either type is a wrapper, and the other type is the corresponding primitive type,
     * this method returns {@code true}. If the given types are not directly assignable, and neither type is a wrapper
     * of the other type, this method returns {@code false}.
     *
     * @param source The source type
     * @param target The target type
     * @return Whether the given source type is assignable to the given target type
     */
    public static boolean isAssignable(Class<?> source, Class<?> target) {
        if (target.isAssignableFrom(source)) {
            return true;
        }
        if (target.isPrimitive() && TypeUtils.isPrimitiveWrapper(source, target)) {
            return true;
        }
        return source.isPrimitive() && TypeUtils.isPrimitiveWrapper(target, source);
    }

    /**
     * Returns a map of the attributes of the given annotation. The keys of the map are the names of the attributes,
     * and the values are the values of the attributes.
     *
     * @param annotation The annotation to get the attributes of
     * @return A map of the attributes of the given annotation
     */
    public static Map<String, Object> getAttributes(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        return Arrays.stream(annotationType.getDeclaredMethods())
                .filter(method -> !method.isAnnotationPresent(Deprecated.class))
                .collect(Collectors.toMap(
                        Method::getName,
                        method -> {
                            try {
                                return method.invoke(annotation);
                            }
                            catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ));
    }

    /**
     * Returns the class of the given instance. This method is a null-safe and type-safe alternative
     * to {@link Object#getClass()}.
     *
     * @param instance The instance to get the class of, or {@code null} if the instance is {@code null}
     * @param <T> The type of the instance
     *
     * @return The class of the given instance
     */
    public static <T> Class<T> getClass(T instance) {
        if (instance == null) {
            return null;
        }
        //noinspection unchecked
        return (Class<T>) instance.getClass();
    }

    public static <T> Option<Class<T>> forName(String name) {
        try {
            //noinspection unchecked
            return Option.of((Class<T>) Class.forName(name));
        }
        catch (ClassNotFoundException e) {
            return Option.empty();
        }
    }

    public static Option<RetentionPolicy> retention(Class<? extends Annotation> annotation) {
        return Option.of(annotation.getAnnotation(Retention.class))
            .map(Retention::value);
    }

    public static boolean hasRetentionPolicy(Class<? extends Annotation> annotation, RetentionPolicy policy) {
        return retention(annotation).test(policy::equals);
    }

    public static boolean hasRetentionPolicy(Set<Class<? extends Annotation>> annotations, RetentionPolicy policy) {
        return annotations.stream().allMatch(annotation -> hasRetentionPolicy(annotation, policy));
    }
}
