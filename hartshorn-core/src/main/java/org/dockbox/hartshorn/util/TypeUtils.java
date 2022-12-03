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

import org.dockbox.hartshorn.util.introspect.annotations.MapBackedAnnotationInvocationHandler;
import org.dockbox.hartshorn.util.introspect.annotations.NotPrimitiveException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
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

    public static <A extends Annotation> A annotation(final Class<A> annotationType) {
        return annotation(annotationType, Collections.emptyMap());
    }

    public static <A extends Annotation> A annotation(final Class<A> annotationType, final Map<String, Object> values) {
        final Object instance = Proxy.newProxyInstance(annotationType.getClassLoader(),
                new Class[] { annotationType },
                new MapBackedAnnotationInvocationHandler(annotationType, values == null ? Collections.emptyMap() : values));
        return adjustWildcards(instance, annotationType);
    }

    public static <T> T checkWrapping(final Object objectToTransform, final TypeView<T> targetType) {
        if (targetType.isInstance(objectToTransform)) return targetType.cast(objectToTransform);
        if (objectToTransform instanceof Collection<?> collection) return checkCollectionWrapping(collection, targetType);
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
            if (defaultConstructor.absent()) throw new IllegalArgumentException("Cannot convert null to " + targetType.name() + ", no default constructor");
            final Attempt<?, Throwable> attempt = defaultConstructor.get().create();
            final Collection<Object> collection = TypeUtils.adjustWildcards(attempt.rethrowUnchecked().get(), Object.class);
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
            final Collection<?> transformed = CollectionUtilities.transform(collectionToTransform, null, adjustWildcards(targetType, TypeView.class));
            return TypeUtils.adjustWildcards(transformed, Object.class);
        }
        else if (targetType.is(Optional.class)) {
            if (collectionToTransform.size() > 1) throw new IllegalArgumentException("Cannot convert collection to optional, collection size is greater than 1");
            return TypeUtils.adjustWildcards(collectionToTransform.stream().findFirst(), Object.class);
        }
        else if (targetType.is(Option.class)) {
            if (collectionToTransform.size() > 1) throw new IllegalArgumentException("Cannot convert collection to option, collection size is greater than 1");
            return TypeUtils.adjustWildcards(Option.of(collectionToTransform.stream().findFirst()), Object.class);
        }
        else if (targetType.is(Attempt.class)) {
            Attempt<?, ?> attempt;
            if (collectionToTransform.size() > 1) attempt = Attempt.of(new IllegalArgumentException("Cannot convert collection to attempt, collection size is greater than 1"));
            attempt = Attempt.of(collectionToTransform.stream().findFirst());
            return TypeUtils.adjustWildcards(attempt, Object.class);
        }
        else {
            if (collectionToTransform.size() > 1) throw new IllegalArgumentException("Cannot convert collection to single value, collection size is greater than 1");
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
            if (defaultConstructor.absent()) throw new IllegalArgumentException("Cannot convert null to " + targetType.name() + ", no default constructor");
            return TypeUtils.adjustWildcards(defaultConstructor.get().create().rethrowUnchecked().get(), Object.class);
        }
        return null;
    }
}
