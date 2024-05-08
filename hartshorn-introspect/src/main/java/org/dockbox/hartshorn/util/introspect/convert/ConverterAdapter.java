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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;

/**
 * A generic converter that delegates to a {@link Converter} instance. This is useful when a
 * {@link ConverterCache} is used to access or implement a {@link ConverterRegistry}.
 *
 * <p>This converter is conditional and only matches if the {@link #convertibleTypes() convertible types}
 * match the source and target types. If the {@link Converter} instance also implements
 * {@link ConditionalConverter}, then the {@link ConditionalConverter#canConvert(Object, Class)}
 * method is used to further narrow the match.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ConverterAdapter implements GenericConverter, ConditionalConverter {

    private final Converter<?, ?> converter;
    private final ConvertibleTypePair typePair;

    public <I, O> ConverterAdapter(Class<I> sourceType, Class<O> targetType, Converter<I, O> converter) {
        this.converter = converter;
        this.typePair = ConvertibleTypePair.of(sourceType, targetType);
    }

    @Override
    public boolean canConvert(Object source, Class<?> targetType) {
        boolean matches = true;
        if (this.converter instanceof ConditionalConverter conditionalConverter) {
            matches = conditionalConverter.canConvert(source, targetType);
        }
        if (matches) {
            if (this.typePair.targetType() != targetType) {
                matches = false;
            }
            else if (!this.typePair.sourceType().isAssignableFrom(source.getClass())) {
                matches = false;
            }
        }
        return matches;
    }

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(this.typePair);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <I, O> Object convert(@Nullable Object source, @NonNull Class<I> sourceType, @NonNull Class<O> targetType) {
        Converter<I, O> ioConverter = (Converter<I, O>) this.converter;
        return ioConverter.convert(sourceType.cast(source));
    }
}
