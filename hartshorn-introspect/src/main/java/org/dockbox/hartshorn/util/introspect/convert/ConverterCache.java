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

/**
 * A cache of converters which can be used to find a converter for a given source and target type.
 * This is useful to serve as middle layer between a {@link ConversionService} and a {@link ConverterRegistry}.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ConverterCache {

    /**
     * Adds a converter to the cache. The converter may be checked for conflicts with other converters,
     * and may be rejected if it conflicts with an existing converter. This remains up to the implementation.
     *
     * @param converter The converter to add
     */
    void addConverter(GenericConverter converter);

    /**
     * Attempts to find a converter for the given source and target type. If no converter is found, {@code null}
     * is returned. If multiple converters are found, a {@link AmbiguousConverterException} may be thrown, or the
     * first instance may be returned. This remains up to the implementation.
     *
     * @param source The source object
     * @param targetType The target type
     * @return The converter, or {@code null} if no converter is found
     * @throws AmbiguousConverterException If multiple converters are found
     */
    GenericConverter getConverter(Object source, Class<?> targetType);

    /**
     * Returns all converters registered in this cache. This may be an empty set, but never {@code null}.
     *
     * @return All converters registered in this cache
     */
    Set<GenericConverter> converters();
}
