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

package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

/**
 * Represents a mapping of an HTTP method and a path pattern, typically used in routing to define
 * the criteria for matching incoming requests to their corresponding handlers. The path pattern may
 * include path variables (e.g., /users/{id}) that can be extracted and used as parameters for the
 * handler.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface RouteMapping {

    /**
     * Gets the HTTP method associated with this route mapping.
     *
     * @return the HTTP method of this route mapping
     */
    HttpMethod method();

    /**
     * Gets the path pattern associated with this route mapping. The path pattern may include path
     * variables (e.g., /users/{id}) that can be extracted and used as parameters for the handler.
     *
     * @return the path pattern of this route mapping
     */
    String pathPattern();

    /**
     * Creates a new {@link RouteMapping} instance with the given HTTP method and path pattern.
     *
     * @param method the HTTP method of the route mapping
     * @param pathPattern the path pattern of the route mapping, which may include path variables
     *
     * @return a new {@link RouteMapping} instance with the specified HTTP method and path pattern
     */
    static RouteMapping of(HttpMethod method, String pathPattern) {
        return new SimpleRouteMapping(method, pathPattern);
    }
}
