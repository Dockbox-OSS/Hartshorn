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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.inject.ExceptionHandler;
import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.Fuzzy;
import org.dockbox.hartshorn.inject.annotations.Required;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.configuration.Scoped;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.condition.support.RequiresProperty;
import org.dockbox.hartshorn.launchpad.annotations.LoggerMeta;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.filter.ErrorCaptureRequestFilter;
import org.dockbox.hartshorn.web.filter.PathMatchingRequestFilter;
import org.dockbox.hartshorn.web.filter.RequestFilter;
import org.dockbox.hartshorn.web.filter.RequestFilterChain;
import org.dockbox.hartshorn.web.filter.RequestLoggingFilter;
import org.dockbox.hartshorn.web.filter.SimpleRequestFilterChain;
import org.dockbox.hartshorn.web.filter.UncapturedRequestFilter;
import org.dockbox.hartshorn.web.message.ObjectMapperResponseWriter;
import org.dockbox.hartshorn.web.message.ResponseWriter;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.report.WebServerDiagnosticsReporter;
import org.dockbox.hartshorn.web.route.PathMatchingRouterPathConfigurer;
import org.dockbox.hartshorn.web.route.PathRouteRegistry;
import org.dockbox.hartshorn.web.route.RouterPathConfigurer;
import org.slf4j.Logger;
import tools.jackson.databind.json.JsonMapper;

import java.util.Comparator;
import java.util.Objects;

/**
 * Configuration class for setting up the web server components, including request filters,
 * route registry, and response writers.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseWebServer.class)
public class WebServerConfiguration {

    /**
     * Creates a {@link RequestFilterChain}, collecting all registered {@link RequestFilter filters}
     * and sorting them by their order.
     *
     * @param filters The collection of request filters.
     *
     * @return A sorted request filter chain.
     */
    @Singleton
    public RequestFilterChain requestHandlerChain(
        @Fuzzy ComponentCollection<RequestFilter> filters
    ) {
        return new SimpleRequestFilterChain(filters.stream()
                .sorted(Comparator.comparingInt(RequestFilter::order))
                .toList());
    }

    /**
     * Creates an {@link ErrorCaptureRequestFilter} that uses the provided {@link ExceptionHandler}
     * to handle exceptions during request processing.
     *
     * @param exceptionHandler The exception handler to use.
     *
     * @return An error capture request filter.
     */
    @Singleton
    @CompositeMember
    public RequestFilter errorCaptureRequestFilter(ExceptionHandler exceptionHandler) {
        return new ErrorCaptureRequestFilter(exceptionHandler);
    }

    /**
     * Creates a {@link PathMatchingRequestFilter} that uses the provided {@link PathRouteRegistry}
     * to match incoming requests to registered routes.
     *
     * @param registry The path route registry to use.
     *
     * @return A path matching request filter.
     */
    @Singleton
    @CompositeMember
    public RequestFilter pathMatchingRequestFilter(
            PathRouteRegistry registry
    ) {
        return new PathMatchingRequestFilter(registry);
    }

    /**
     * Creates a {@link RequestLoggingFilter} if request logging is enabled via configuration.
     *
     * @param builder An optional builder for the request logging filter.
     * @param logger The logger to use for logging requests.
     *
     * @return A request logging filter.
     */
    @Singleton
    @CompositeMember
    @RequiresProperty(name = "hartshorn.web.logging.enabled", withValue = "true")
    public RequestFilter requestLoggingFilter(
            @Required(false) RequestLoggingFilter.Builder builder,
            @LoggerMeta(context = RequestLoggingFilter.class) Logger logger
    ) {
        return Objects.requireNonNullElseGet(
                builder,
                RequestLoggingFilter::builder
        ).build(logger);
    }

    /**
     * Creates an {@link UncapturedRequestFilter} to handle requests that do not match any
     * registered routes.
     *
     * @return An uncaptured request filter.
     */
    @Singleton
    @CompositeMember
    public RequestFilter uncapturedRequestFilter() {
        return new UncapturedRequestFilter();
    }

    /**
     * Creates a {@link PathRouteRegistry} and applies all registered customizers to it.
     *
     * @param customizers The collection of customizers for the path route registry.
     *
     * @return A configured path route registry.
     */
    @Singleton
    public PathRouteRegistry pathRouteRegistry(
            @Fuzzy ComponentCollection<Customizer<PathRouteRegistry>> customizers
    ) {
        PathRouteRegistry registry = new PathRouteRegistry();
        customizers.forEach(customizer -> customizer.configure(registry));
        return registry;
    }

    /**
     * Creates a customizer for the {@link PathRouteRegistry} that applies all registered
     * {@link RouterPathConfigurer path configurers}.
     *
     * @param pathCustomizers The collection of path configurers.
     *
     * @return A customizer for the path route registry.
     */
    @Singleton
    @CompositeMember
    Customizer<PathRouteRegistry> routeCustomizer(
            @Fuzzy ComponentCollection<Customizer<RouterPathConfigurer>> pathCustomizers
    ) {
        return registry -> {
            RouterPathConfigurer configurer = new PathMatchingRouterPathConfigurer(registry);
            pathCustomizers.forEach(customizer -> customizer.configure(configurer));
        };
    }

    /**
     * Creates a {@link WebServerBootstrap} lifecycle observer to handle web server startup and
     * shutdown.
     *
     * @return A web server bootstrap lifecycle observer.
     */
    @Singleton
    @CompositeMember
    public LifecycleObserver webServerBootstrap() {
        return new WebServerBootstrap();
    }

    /**
     * Creates a {@link ResponseWriter} that uses a {@link JsonMapper} to serialize objects to
     * JSON.
     *
     * @param jsonMapper The JSON mapper to use for serialization.
     *
     * @return A response writer for JSON objects.
     */
    @Singleton
    public ResponseWriter<Object> objectMapperResponseWriter(JsonMapper jsonMapper) {
        return new ObjectMapperResponseWriter(jsonMapper, "application/json");
    }

    /**
     * Provides the current {@link WebRequest} from the {@link WebRequestScope}.
     *
     * @param scope The web request scope.
     *
     * @return The current web request.
     */
    @Prototype
    @Scoped(WebRequestScope.class)
    public WebRequest webRequest(WebRequestScope scope) {
        return scope.request();
    }

    /**
     * Provides the current {@link WebResponse} from the {@link WebRequestScope}.
     *
     * @param scope The web request scope.
     *
     * @return The current web response.
     */
    @Prototype
    @Scoped(WebRequestScope.class)
    public WebResponse webResponse(WebRequestScope scope) {
        return scope.response();
    }

    @Singleton
    @CompositeMember
    public CategorizedDiagnosticsReporter webServerDiagnosticsReporter(WebServer webServer) {
        return new WebServerDiagnosticsReporter(webServer);
    }
}
