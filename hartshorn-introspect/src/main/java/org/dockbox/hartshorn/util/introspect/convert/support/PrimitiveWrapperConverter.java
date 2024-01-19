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

import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;

/**
 * Converts primitive wrapper types to their corresponding primitive types and vice versa. For example, converts
 * {@link Integer} to {@code int} and {@code int} to {@link Integer}.
 *
 * @since 0.5.0
 *
 * @see TypeUtils#isPrimitiveWrapper(Class, Class)
 *
 * @author Guus Lieben
 */
public class PrimitiveWrapperConverter implements GenericConverter, ConditionalConverter {

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return null;
    }

    @Override
    public boolean canConvert(Object source, Class<?> targetType) {
        if (source == null) {
            return false;
        }
        Class<?> sourceType = source.getClass();
        boolean primitiveToWrapper = sourceType.isPrimitive() && TypeUtils.isPrimitiveWrapper(targetType, sourceType);
        if (primitiveToWrapper) {
            return true;
        }
        // Wrapper to primitive
        return targetType.isPrimitive() && TypeUtils.isPrimitiveWrapper(sourceType, targetType);
    }

    @Override
    public @Nullable <I, O> Object convert(@Nullable Object source, @NonNull Class<I> sourceType, @NonNull Class<O> targetType) {
        assert source != null;
        // Implicit (un)boxing
        return source;
    }
}
