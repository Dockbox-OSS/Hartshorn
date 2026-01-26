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
 * A configurer for defining routes with specific HTTP methods and paths.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface RouterPathConfigurer {

    /**
     * Defines a route for the specified HTTP method and path, associating it with the given request
     * handler.
     *
     * @param method The HTTP method for the route.
     * @param path The path for the route.
     * @param handler The request handler to handle requests to this route.
     *
     * @return The current {@link RouterPathConfigurer} instance for method chaining.
     */
    RouterPathConfigurer request(HttpMethod method, String path, RequestHandler handler);

    /**
     * Defines a GET route for the specified path, associating it with the given request handler.
     *
     * @param path The path for the GET route.
     * @param handler The request handler to handle GET requests to this route.
     *
     * @return The current {@link RouterPathConfigurer} instance for method chaining.
     */
    default RouterPathConfigurer get(String path, RequestHandler handler) {
        return this.request(HttpMethod.GET, path, handler);
    }

    /**
     * Defines a POST route for the specified path, associating it with the given request handler.
     *
     * @param path The path for the POST route.
     * @param handler The request handler to handle POST requests to this route.
     *
     * @return The current {@link RouterPathConfigurer} instance for method chaining.
     */
    default RouterPathConfigurer post(String path, RequestHandler handler) {
        return this.request(HttpMethod.POST, path, handler);
    }

    /**
     * Defines a PUT route for the specified path, associating it with the given request handler.
     *
     * @param path The path for the PUT route.
     * @param handler The request handler to handle PUT requests to this route.
     *
     * @return The current {@link RouterPathConfigurer} instance for method chaining.
     */
    default RouterPathConfigurer put(String path, RequestHandler handler) {
        return this.request(HttpMethod.PUT, path, handler);
    }

    /**
     * Defines a DELETE route for the specified path, associating it with the given request handler.
     *
     * @param path The path for the DELETE route.
     * @param handler The request handler to handle DELETE requests to this route.
     *
     * @return The current {@link RouterPathConfigurer} instance for method chaining.
     */
    default RouterPathConfigurer delete(String path, RequestHandler handler) {
        return this.request(HttpMethod.DELETE, path, handler);
    }
}
