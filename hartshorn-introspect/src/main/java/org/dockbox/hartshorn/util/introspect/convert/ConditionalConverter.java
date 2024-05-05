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

/**
 * A converter which can add additional conditions to the conversion process. This is useful for
 * converters which can convert a source type to a target type, but only under certain conditions.
 *
 * <p>This interface does not directly extend {@link GenericConverter} as it is intended to also
 * support {@link Converter}, {@link ConverterFactory}, {@link DefaultValueProvider}, and
 * {@link DefaultValueProviderFactory} implementations.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ConditionalConverter {

    /**
     * Returns whether this converter can convert the given source object to the target type.
     *
     * @param source the source object to convert. Will only be {@code null} if this is a {@link DefaultValueProvider}
     * @param targetType the target type to convert to
     * @return {@code true} if this converter can perform the conversion; {@code false} otherwise
     */
    boolean canConvert(Object source, Class<?> targetType);
}
