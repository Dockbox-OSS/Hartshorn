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
 * A registrar to register {@link RequestHandler request handlers} for specific
 * {@link RouteMapping route mappings}, typically to a {@link HandlerMappingRegistry}. Registrars
 * are typically provided by the framework, and should only be customized through
 * {@link RouterCustomizer router customizers}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HandlerMappingRegistrar {

    /**
     * Registers the given {@link RequestHandler} for the given {@link RouteMapping}.
     *
     * @param mapping the request mapping to register the handler for
     * @param handler the request handler
     *
     * @return this registrar, for chaining
     */
    HandlerMappingRegistrar add(RouteMapping mapping, RequestHandler handler);

    /**
     * Registers the given {@link RequestHandler} as a {@link HttpMethod#GET GET} handler for the
     * given path pattern.
     *
     * @param pathPattern the path pattern to register the handler for
     * @param handler the request handler
     *
     * @return this registrar, for chaining
     */
    default HandlerMappingRegistrar get(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.GET, pathPattern), handler);
    }

    /**
     * Registers the given {@link RequestHandler} as a {@link HttpMethod#POST POST} handler for the
     * given path pattern.
     *
     * @param pathPattern the path pattern to register the handler for
     * @param handler the request handler
     *
     * @return this registrar, for chaining
     */
    default HandlerMappingRegistrar post(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.POST, pathPattern), handler);
    }

    /**
     * Registers the given {@link RequestHandler} as a {@link HttpMethod#PUT PUT} handler for the
     * given path pattern.
     *
     * @param pathPattern the path pattern to register the handler for
     * @param handler the request handler
     *
     * @return this registrar, for chaining
     */
    default HandlerMappingRegistrar put(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.PUT, pathPattern), handler);
    }

    /**
     * Registers the given {@link RequestHandler} as a {@link HttpMethod#DELETE DELETE} handler for
     * the given path pattern.
     *
     * @param pathPattern the path pattern to register the handler for
     * @param handler the request handler
     *
     * @return this registrar, for chaining
     */
    default HandlerMappingRegistrar delete(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.DELETE, pathPattern), handler);
    }

    /**
     * Registers the given {@link RequestHandler} as a {@link HttpMethod#PATCH PATCH} handler for
     * the given path pattern.
     *
     * @param pathPattern the path pattern to register the handler for
     * @param handler the request handler
     *
     * @return this registrar, for chaining
     */
    default HandlerMappingRegistrar patch(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.PATCH, pathPattern), handler);
    }
}
