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

package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.TypeConversionException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.IntrospectorAwareView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

public final class IntrospectionTypeUtils {

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
            final TypeView<?> collectionType = adjustCollectionType(targetType);
            final Option<? extends ConstructorView<?>> defaultConstructor = collectionType.constructors().defaultConstructor();

            if (defaultConstructor.absent()) throw new IllegalArgumentException("Cannot convert null to " + targetType.name() + ", no default constructor");
            final Attempt<?, Throwable> attempt = defaultConstructor.get().create();
            final Collection<Object> collection = TypeUtils.adjustWildcards(attempt
                            .mapError(e -> new TypeConversionException(targetType.type(), "Unexpected exception while creating collection of type " + targetType.name(), e))
                            .get(),
                    Object.class
            );

            collection.add(value);
            return TypeUtils.adjustWildcards(collection, Object.class);
        }
        throw new IllegalArgumentException("Cannot convert " + objectToTransform.getClass().getName() + " to " + targetType.name());
    }

    private static TypeView<?> adjustCollectionType(final TypeView<?> collectionType) {
        if (collectionType instanceof IntrospectorAwareView introspectorAwareView) {
            final Introspector introspector = introspectorAwareView.introspector();
            final Supplier<Collection<?>> supplier = CollectionUtilities.COLLECTION_DEFAULTS.get(collectionType.type());
            if (supplier != null) {
                final Collection<?> collection = supplier.get();
                return introspector.introspect(collection);
            }
        }
        return collectionType;
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
            final Collection<?> transformed = transform(collectionToTransform, null, TypeUtils.adjustWildcards(targetType, TypeView.class));
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
            for (final Entry<Class<?>, Supplier<Collection<?>>> entry : CollectionUtilities.COLLECTION_DEFAULTS.entrySet()) {
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
                final Option<? extends Collection<?>> createdCollection = fromCollectionConstructor.get().create(List.of(collection));
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
