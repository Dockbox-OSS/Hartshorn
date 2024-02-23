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

package org.dockbox.hartshorn.util.introspect.convert.support;

import java.lang.reflect.Array;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;

/**
 * Converts any array to an {@link Object} by returning the first and only element of the
 * array. The array must have exactly one element.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ArrayToObjectConverter implements GenericConverter, ConditionalConverter {

    private final ConversionService conversionService;

    public ArrayToObjectConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(ConvertibleTypePair.of(Object[].class, Object.class));
    }

    @Override
    public boolean canConvert(Object source, Class<?> targetType) {
        return source != null && source.getClass().isArray() && Array.getLength(source) == 1;
    }

    @Override
    public @Nullable <I, O> Object convert(@Nullable Object source, @NonNull Class<I> sourceType, @NonNull Class<O> targetType) {
        if (sourceType.isArray()) {
            if (Array.getLength(source) != 1) {
                return null;
            }

            Object firstElement = Array.get(source, 0);
            if (firstElement == null) {
                return null;
            }
            else if (targetType.isAssignableFrom(firstElement.getClass())) {
                return firstElement;
            }
            else {
                return this.conversionService.convert(firstElement, targetType);
            }
        }
        return null;
    }
}
