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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

/**
 * Converts a {@link String} to an {@link Enum}. The {@link Enum} constant is matched by name in a case-sensitive
 * manner. If no match is found, {@code null} is returned.
 *
 * @since 0.5.0
 *
 * @see Enum#valueOf(Class, String)
 *
 * @author Guus Lieben
 */
@SuppressWarnings("rawtypes")
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <O extends Enum> Converter<String, O> create(Class<O> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    /**
     * Converts a {@link String} to an {@link Enum}. The {@link Enum} constant is matched by name in a case-sensitive
     * manner. Extraneous whitespace is trimmed. If no match is found, {@code null} is returned.
     *
     * @param enumType The type of {@link Enum} to convert to
     * @param <T> The type of {@link Enum} to convert to
     *
     * @since 0.5.0
     *
     * @see Enum#valueOf(Class, String)
     *
     * @author Guus Lieben
     */
    @SuppressWarnings("unchecked")
    private record StringToEnumConverter<T extends Enum>(Class<T> enumType) implements Converter<String, T> {

        @Override
        public @Nullable T convert(@Nullable String source) {
            assert source != null;
            if(source.isEmpty()) {
                return null;
            }
            try {
                Enum value = Enum.valueOf(this.enumType, source.trim());
                return this.enumType.cast(value);
            }
            catch(IllegalArgumentException e) {
                return null;
            }
        }
    }
}
