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
 * An interface representing a service for object converters. This service provides a standardized way of
 * converting objects from one type to another in Java applications. The service provides methods for
 * checking whether a conversion is possible and for actually performing the conversion, and supports a
 * variety of different types of converters. The types of converters that can be used with this service are:
 * <ul>
 *    <li>{@link GenericConverter}</li>
 *    <li>{@link Converter}</li>
 *    <li>{@link ConverterFactory}</li>
 *    <li>{@link DefaultValueProvider}</li>
 *    <li>{@link DefaultValueProviderFactory}</li>
 * </ul>
 *
 * This service is particularly useful in situations where object conversion is a common task, such as
 * when processing user input data or when converting between different data formats in web controllers.
 *
 * <p>When either the {@link #canConvert} or {@link #convert} method is called with a {@code null} value
 * for the source or input parameter, the service may attempt to invoke a {@link DefaultValueProvider} to
 * obtain a default value for the input. It is important to note that not all {@link ConversionService}
 * implementations may support {@link DefaultValueProvider} instances, and whether a {@link DefaultValueProvider}
 * is actually invoked for a {@code null} input will depend on the specific implementation being used.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ConversionService {

    /**
     * Returns whether this service can convert an object of the given source type to the given target type.
     * This may be based on the presence of a converter, or on the ability to create a converter on the fly.
     *
     * @param source the source type to convert from
     * @param targetType the target type to convert to
     * @return {@code true} if a conversion can be performed; {@code false} otherwise
     */
    boolean canConvert(Object source, Class<?> targetType);

    /**
     * Converts the given object to the given target type. This method will first check whether a conversion
     * is possible, and if so, will perform the conversion. If no conversion is possible, a
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param input the object to convert
     * @param targetType the target type to convert to
     * @return the converted object, may be {@code null}
     * @param <I> the input type
     * @param <O> the output type
     * @throws IllegalArgumentException if no conversion is possible
     */
    <I, O> O convert(I input, Class<O> targetType);
}
