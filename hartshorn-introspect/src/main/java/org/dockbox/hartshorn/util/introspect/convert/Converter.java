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

package org.dockbox.hartshorn.util.introspect.convert;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A functional interface for converting objects from one type to another. This interface provides
 * a single method for converting an input object of type {@link I} to an output object of type
 * {@link O}. The input object may be nullable, and the output object may be nullable as well. If
 * the input object is {@code null}, the {@link #convert(Object)} method may return null, or provide
 * a default value.
 *
 * <p>Implementations of this interface are intended to be used as converters in a {@link ConversionService}
 * instance or in other contexts where a functional-style converter is required.
 *
 * @param <I> the input object type
 * @param <O> the output object type
 */
@FunctionalInterface
public interface Converter<I, O> {

    /**
     * Convert the input object of type {@link I} to an output object of type {@link O}. The input
     * object may be {@code null}, in which case the implementation may choose to return a default
     * value, or {@code null} itself.
     *
     * @param input the input object to convert, which may be {@code null}
     * @return the converted object, which may be {@code null}
     */
    @Nullable
    O convert(@Nullable I input);

    /**
     * Return a composed converter that first applies this converter to its input, and then applies
     * the {@code after} converter to the result. If evaluation of either converter throws an exception,
     * it is relayed to the caller of the composed converter.
     *
     * @param after the converter to apply after this converter is applied
     * @return a composed converter that first applies this converter and then applies the {@code after}
     * @param <T> the type of output of the {@code after} converter, and of the composed converter
     */
    default <T> Converter<I, T> andThen(final Converter<O, T> after) {
        return (final I i) -> {
            final O result = this.convert(i);
            return result != null ? after.convert(result) : null;
        };
    }
}
