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

package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;

import java.lang.reflect.Array;
import java.util.Set;

public class ArrayToObjectConverter implements GenericConverter {

    private final ConversionService conversionService;

    public ArrayToObjectConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(ConvertibleTypePair.of(Object[].class, Object.class));
    }

    @Override
    public @Nullable <I, O> Object convert(final @Nullable Object source, final @NonNull Class<I> sourceType, final @NonNull Class<O> targetType) {
        if (sourceType.isArray()) {
            if (Array.getLength(source) != 1) {
                return null;
            }

            final Object firstElement = Array.get(source, 0);
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
