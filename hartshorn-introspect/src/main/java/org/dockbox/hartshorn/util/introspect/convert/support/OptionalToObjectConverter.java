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

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

/**
 * Converts an {@link Optional} to an {@link Object}. If the optional is empty, {@code null} is returned.
 * Otherwise, the value of the optional is returned.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class OptionalToObjectConverter implements Converter<Optional<?>, Object>, ConditionalConverter {

    @Override
    public @Nullable Object convert(@Nullable Optional<?> input) {
        assert input != null;
        return input.orElse(null);
    }

    @Override
    public boolean canConvert(Object source, Class<?> targetType) {
        if (source instanceof Optional<?> optional) {
            Object value = optional.orElse(null);

            if (value == null) {
                return true;
            }
            return TypeUtils.isAssignable(value.getClass(), targetType);
        }
        return false;
    }
}
