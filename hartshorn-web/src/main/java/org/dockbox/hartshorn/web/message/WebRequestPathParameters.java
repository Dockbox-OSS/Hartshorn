/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

/**
 * Represents the path parameters of a web request.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface WebRequestPathParameters {

    /**
     * Returns a map representation of the path parameters.
     *
     * @return A map containing all path parameters.
     */
    Map<String, String> asMap();

    /**
     * Retrieves the value of the path parameter with the given name.
     *
     * @param name The name of the path parameter.
     *
     * @return An {@link Option} containing the value of the path parameter, or an empty option if
     * the parameter is not present.
     */
    Option<String> pathParameter(String name);

    /**
     * Retrieves the value of the path parameter with the given name, converted to the specified
     * type.
     *
     * @param name The name of the path parameter.
     * @param converter The converter to use for converting the parameter value.
     * @param <T> The type to convert the parameter value to.
     *
     * @return An {@link Option} containing the converted value of the path parameter, or an empty
     * option if the parameter is not present or conversion fails.
     */
    <T> Option<T> pathParameter(String name, Converter<String, T> converter);
}
