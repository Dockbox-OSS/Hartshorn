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

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

/**
 * Represents the query parameters of an HTTP message.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HttpMessageQuery {

    /**
     * Retrieves the value of the query parameter with the given name. If multiple values are
     * present for the same name, they are concatenated using a comma as a separator.
     *
     * @param name The name of the query parameter.
     *
     * @return An {@link Option} containing the value of the query parameter, or an empty option if
     * the parameter is not present.
     */
    Option<String> get(String name);

    /**
     * Retrieves all values of the query parameter with the given name.
     *
     * @param name The name of the query parameter.
     *
     * @return A list of all values associated with the query parameter. If the parameter is not
     * present, an empty list is returned.
     */
    List<String> getAll(String name);

    /**
     * Retrieves all query parameters as a {@link MultiMap}.
     *
     * @return A {@link MultiMap} containing all query parameters.
     */
    MultiMap<String, String> asMultiMap();

    /**
     * Checks if the query contains any parameters.
     *
     * @return {@code true} if the query contains at least one parameter, {@code false} otherwise.
     */
    boolean notEmpty();

    /**
     * Converts the query parameters to a string representation.
     *
     * @return A string representation of the query parameters.
     */
    String asString();
}
