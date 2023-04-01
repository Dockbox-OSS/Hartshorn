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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

@SuppressWarnings("rawtypes")
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <O extends Enum> Converter<String, O> create(final Class<O> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    @SuppressWarnings("unchecked")
    private static class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        private StringToEnumConverter(final Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public @Nullable T convert(final @Nullable String source) {
            assert source != null;
            if (source.isEmpty()) {
                return null;
            }
            try {
                final Enum value = Enum.valueOf(this.enumType, source.trim());
                return this.enumType.cast(value);
            }
            catch (final IllegalArgumentException e) {
                return null;
            }
        }
    }
}
