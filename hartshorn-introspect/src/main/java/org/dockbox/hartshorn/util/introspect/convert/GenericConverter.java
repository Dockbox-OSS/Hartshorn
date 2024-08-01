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

package org.dockbox.hartshorn.util.introspect.convert;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A generic converter interface that can be used to convert objects from one type to another. This
 * interface can be used either directly, or through a {@link ConversionService}. Implementations
 * should be thread-safe.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface GenericConverter {

    /**
     * Returns the set of {@link ConvertibleTypePair} that this converter can convert between. This method
     * is primarily intended to be used for introspection purposes through a {@link ConversionService}. This
     * allows the service to discover all converters that are available for a given conversion task.
     *
     * <p>If this converter is {@link ConditionalConverter conditional}, then this method may return
     * {@code null} to indicate that it does not declare a specific source-to-target conversion pair.
     *
     * @return the set of convertible type pairs
     */
    Set<ConvertibleTypePair> convertibleTypes();

    /**
     * Convert the source object to the specified target type. The implementation should return {@code null}
     * if the source cannot be converted to the specified target type.
     *
     * @param source the source object to convert
     * @param sourceType the type descriptor of the source object
     * @param targetType the type descriptor of the target object, which is to be created
     * @return the converted object, or {@code null} if the conversion cannot be performed
     * @param <I> the source type
     * @param <O> the target type
     */
    <I, O> @Nullable Object convert(@Nullable Object source, @NonNull Class<I> sourceType, @NonNull Class<O> targetType);

}
