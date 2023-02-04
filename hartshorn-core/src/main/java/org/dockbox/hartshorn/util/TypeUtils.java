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

import org.dockbox.hartshorn.util.introspect.annotations.MapBackedAnnotationInvocationHandler;
import org.dockbox.hartshorn.util.introspect.annotations.NotPrimitiveException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
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

    private static final Map<Class<?>, Supplier<Collection<?>>> COLLECTION_DEFAULTS = Map.ofEntries(
            Map.entry(Collection.class, ArrayList::new),
            Map.entry(List.class, ArrayList::new),
            Map.entry(Set.class, HashSet::new)
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

    public static <T> T checkWrapping(final Object objectToTransform, final TypeView<T> targetType) {
        if (targetType.isInstance(objectToTransform)) return targetType.cast(objectToTransform);
        if (objectToTransform instanceof Collection<?> collection)
            return checkCollectionWrapping(collection, targetType);
        else if (objectToTransform == null) return createAdjustedEmptyWrapper(targetType);
        else return checkSingleValueWrapping(objectToTransform, targetType);
    }

    private static <T> T checkSingleValueWrapping(final Object objectToTransform, final TypeView<T> targetType) {
        final Object value = unwrapSingleValue(objectToTransform);
        if (value == null) return createAdjustedEmptyWrapper(targetType);
        if (targetType.is(Optional.class)) {
            return TypeUtils.adjustWildcards(Optional.of(value), Object.class);
        }
        else if (targetType.is(Option.class)) {
            return TypeUtils.adjustWildcards(Option.of(value), Object.class);
        }
        else if (targetType.is(Attempt.class)) {
            return TypeUtils.adjustWildcards(Attempt.of(value), Object.class);
        }
        else if (targetType.isChildOf(Collection.class)) {
            final Option<? extends ConstructorView<?>> defaultConstructor = targetType.constructors().defaultConstructor();
            if (defaultConstructor.absent())
                throw new IllegalArgumentException("Cannot convert null to " + targetType.name() + ", no default constructor");

            final Attempt<?, Throwable> attempt = defaultConstructor.get().create();
            final Object istance = attempt
                    .mapError(error -> new IllegalArgumentException("Cannot convert null to " + targetType.name() + ", default constructor threw exception", error))
                    .rethrow()
                    .get();

            final Collection<Object> collection = TypeUtils.adjustWildcards(istance, Object.class);
            collection.add(value);
            return TypeUtils.adjustWildcards(collection, Object.class);
        }
        throw new IllegalArgumentException("Cannot convert " + objectToTransform.getClass().getName() + " to " + targetType.name());
    }

    private static Object unwrapSingleValue(final Object objectToUnwrap) {
        if (objectToUnwrap instanceof Optional<?> optional) {
            return optional.orElse(null);
        }
        else if (objectToUnwrap instanceof Attempt<?, ?> attempt) {
            return attempt.orNull();
        }
        else if (objectToUnwrap instanceof Option<?> option) {
            return option.orNull();
        }
        else return objectToUnwrap;
    }

    private static <T> T checkCollectionWrapping(final Collection<?> collectionToTransform, final TypeView<T> targetType) {
        if (collectionToTransform.isEmpty()) {
            return createAdjustedEmptyWrapper(targetType);
        }
        else if (targetType.isChildOf(Collection.class)) {
            final Collection<?> transformed = transform(collectionToTransform, null, adjustWildcards(targetType, TypeView.class));
            return TypeUtils.adjustWildcards(transformed, Object.class);
        }
        else if (targetType.is(Optional.class)) {
            if (collectionToTransform.size() > 1)
                throw new IllegalArgumentException("Cannot convert collection to optional, collection size is greater than 1");
            return TypeUtils.adjustWildcards(collectionToTransform.stream().findFirst(), Object.class);
        }
        else if (targetType.is(Option.class)) {
            if (collectionToTransform.size() > 1)
                throw new IllegalArgumentException("Cannot convert collection to option, collection size is greater than 1");
            return TypeUtils.adjustWildcards(Option.of(collectionToTransform.stream().findFirst()), Object.class);
        }
        else if (targetType.is(Attempt.class)) {
            Attempt<?, ?> attempt;
            if (collectionToTransform.size() > 1)
                attempt = Attempt.of(new IllegalArgumentException("Cannot convert collection to attempt, collection size is greater than 1"));
            attempt = Attempt.of(collectionToTransform.stream().findFirst());
            return TypeUtils.adjustWildcards(attempt, Object.class);
        }
        else {
            if (collectionToTransform.size() > 1)
                throw new IllegalArgumentException("Cannot convert collection to single value, collection size is greater than 1");
            final Object first = collectionToTransform.iterator().next();
            if (first == null) return null;
            if (targetType.isInstance(first)) return TypeUtils.adjustWildcards(first, Object.class);
        }
        throw new IllegalArgumentException("Cannot convert collection to " + targetType.name());
    }

    private static <T> T createAdjustedEmptyWrapper(final TypeView<T> targetType) {
        if (targetType.is(Optional.class)) {
            return TypeUtils.adjustWildcards(Optional.empty(), Object.class);
        }
        else if (targetType.is(Option.class)) {
            return TypeUtils.adjustWildcards(Option.empty(), Object.class);
        }
        else if (targetType.is(Attempt.class)) {
            return TypeUtils.adjustWildcards(Attempt.empty(), Object.class);
        }
        else if (targetType.isChildOf(Collection.class)) {
            final Option<? extends ConstructorView<?>> defaultConstructor = targetType.constructors().defaultConstructor();
            if (defaultConstructor.absent())
                throw new IllegalArgumentException("Cannot convert null to " + targetType.name() + ", no default constructor");
            
            return TypeUtils.adjustWildcards(defaultConstructor.get().create()
                            .mapError(error -> new IllegalArgumentException("Cannot convert null to " + targetType.name() + ", default constructor threw exception", error))
                            .rethrow()
                            .get(),
                    Object.class);
        }
        return null;
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

    // TODO: Move to proper transformer class
    public static <E, T extends Collection<E>> T transform(final Collection<E> collection, final Collection<E> initialCollection, final TypeView<? extends Collection<?>> target) {
        if (initialCollection != null) {
            initialCollection.addAll(collection);
            if (!target.isParentOf(initialCollection.getClass())) {
                throw new IllegalArgumentException("Initial collection is not of the same type as the target collection");
            }
            return TypeUtils.adjustWildcards(initialCollection, Collection.class);
        }
        else if (target.isAbstract()) {
            for (final Entry<Class<?>, Supplier<Collection<?>>> entry : COLLECTION_DEFAULTS.entrySet()) {
                if (target.is(entry.getKey())) {
                    final Collection<E> collectionInstance = TypeUtils.adjustWildcards(entry.getValue().get(), Collection.class);
                    collectionInstance.addAll(collection);
                    return TypeUtils.adjustWildcards(collectionInstance, Collection.class);
                }
            }
            throw new UnsupportedOperationException("Cannot transform to an abstract collection type (was not a Set or List), use a concrete type instead, or assign a default (empty) collection instance.");
        }
        else {
            final Option<? extends ConstructorView<? extends Collection<?>>> fromCollectionConstructor = target.constructors().withParameters(Collection.class);
            if (fromCollectionConstructor.present()) {
                final Option<? extends Collection<?>> createdCollection = fromCollectionConstructor.get().create(collection);
                if (createdCollection.present()) {
                    return TypeUtils.adjustWildcards(createdCollection.get(), Collection.class);
                }
            }
            else {
                final Option<? extends ConstructorView<? extends Collection<?>>> defaultConstructor = target.constructors().defaultConstructor();
                if (defaultConstructor.present()) {
                    final Option<? extends Collection<?>> createdCollection = defaultConstructor.get().create();
                    if (createdCollection.present()) {
                        final Collection<E> collectionInstance = TypeUtils.adjustWildcards(createdCollection.get(), Collection.class);
                        collectionInstance.addAll(collection);
                        return TypeUtils.adjustWildcards(collectionInstance, Collection.class);
                    }
                }
            }
            throw new IllegalArgumentException("Collection type " + target.name() + " does not have a constructor that accepts a Collection, or a default constructor. Can't transform collection to this type.");
        }
    }
}
