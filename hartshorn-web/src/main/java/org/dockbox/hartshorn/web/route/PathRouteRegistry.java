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

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.message.WebRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry for path-based routes, allowing registration and retrieval of request handlers
 * based on HTTP methods and path patterns.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class PathRouteRegistry implements Reportable {

    private final Map<
            HttpMethod,
            Map<String, RequestHandler>
            > methodRoutes = new ConcurrentHashMap<>();

    /**
     * Registers a request handler for the specified HTTP method and path pattern.
     *
     * @param method The HTTP method for which the handler is registered.
     * @param path The path pattern for which the handler is registered.
     * @param handler The request handler to register.
     */
    public void register(HttpMethod method, String path, RequestHandler handler) {
        this.methodRoutes
                .computeIfAbsent(method, _ -> new ConcurrentHashMap<>())
                .put(path, handler);
    }

    /**
     * Retrieves the registered route for the given web request, if any.
     *
     * @param request The web request for which to find a matching route.
     * @return A {@link RegisteredRoute} containing the matched handler and path parameters,
     * or {@code null} if no matching route is found.
     */
    public RegisteredRoute handler(WebRequest request) {
        Map<String, RequestHandler> routes = this.methodRoutes.get(request.method());
        if (routes == null) {
            return null;
        }
        for (Map.Entry<String, RequestHandler> entry : routes.entrySet()) {
            PathPatternMatcher.MatchResult result = PathPatternMatcher.match(
                    entry.getKey(),
                    request.path()
            );
            if (result.matches()) {
                RequestHandler handler = entry.getValue();
                return new RegisteredRoute(
                        request.method(),
                        entry.getKey(),
                        handler,
                        result.parameters()
                );
            }
        }
        return null;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        for (HttpMethod method : HttpMethod.values()) {
            Map<String, RequestHandler> routes = this.methodRoutes.get(method);
            if (routes == null || routes.isEmpty()) {
                continue;
            }
            collector.property(method.name()).writeDelegate(methodCollector -> {
                for (Map.Entry<String, RequestHandler> entry : routes.entrySet()) {
                    DiagnosticsPropertyWriter propertyWriter = methodCollector.property(
                            entry.getKey()
                    );
                    if (entry.getValue() instanceof Reportable reportable) {
                        propertyWriter.writeDelegate(reportable);
                    }
                    else {
                        propertyWriter.writeString(entry.getValue().getClass().getName());
                    }
                }
            });
        }
    }

    /**
     * A record representing a registered route, including the HTTP method, path pattern,
     * request handler, and any extracted path parameters.
     *
     * @param method The HTTP method of the route.
     * @param path The path pattern of the route.
     * @param handler The request handler associated with the route.
     * @param pathParameters A map of path parameters extracted from the request path.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public record RegisteredRoute(
            HttpMethod method,
            String path,
            RequestHandler handler,
            Map<String, String> pathParameters
    ) {}
}
