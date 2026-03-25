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

import java.util.Map;

/**
 * Registry tracking {@link RequestHandler request handlers} for specific
 * {@link RouteMapping route mappings}. This registry is typically configured through
 * {@link HandlerMappingRegistrar registrars}, and is used by the web server to resolve incoming
 * requests to their corresponding handler.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HandlerMappingRegistry {

    /**
     * Registers the given {@link RequestHandler} for the given {@link RouteMapping}.
     *
     * @param mapping the request mapping to register the handler for
     * @param handler the request handler
     */
    void add(RouteMapping mapping, RequestHandler handler);

    /**
     * Returns all currently registered request handlers, identified by their corresponding route
     * mappings.
     *
     * @return all currently registered request handlers
     */
    Map<RouteMapping, RequestHandler> mappings();

    /**
     * Returns all currently registered request handlers for the given HTTP method, identified by
     * their corresponding path patterns.
     *
     * @param method the HTTP method to filter for
     * @return all currently registered request handlers for the given HTTP method
     */
    Map<String, RequestHandler> mappings(HttpMethod method);
}
