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

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.Fuzzy;
import org.dockbox.hartshorn.inject.annotations.Named;
import org.dockbox.hartshorn.inject.annotations.Required;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.configuration.Scoped;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.condition.support.RequiresClass;
import org.dockbox.hartshorn.inject.condition.support.RequiresProperty;
import org.dockbox.hartshorn.launchpad.annotations.LoggerMeta;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.web.filter.RequestLoggingFilter;
import org.dockbox.hartshorn.web.message.ObjectMapperResponseWriter;
import org.dockbox.hartshorn.web.message.ResponseWriter;
import org.dockbox.hartshorn.web.report.WebServerDiagnosticsReporter;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.HandlerMappingResolver;
import org.dockbox.hartshorn.web.route.RouterCustomizer;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.rest.DeclarativeRouterPathConfigurer;
import org.dockbox.hartshorn.web.route.support.PathPatternMatcher;
import org.dockbox.hartshorn.web.route.support.PatternMatchingHandlerMappingResolver;
import org.dockbox.hartshorn.web.route.support.StandardPathPatternMatcher;
import org.slf4j.Logger;
import tools.jackson.databind.json.JsonMapper;

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
    public Filter requestLoggingFilter(
            @Required(false) RequestLoggingFilter.Builder builder,
            @LoggerMeta(context = RequestLoggingFilter.class) Logger logger
    ) {
        return Objects.requireNonNullElseGet(
                builder,
                RequestLoggingFilter::builder
        ).build(logger);
    }

    /**
     * Creates a {@link HandlerMappingRegistry} and applies all registered customizers to it.
     *
     * @param customizers The collection of customizers for the handler mapping registry.
     *
     * @return A configured handler mapping registry.
     */
    @Singleton
    public HandlerMappingRegistry pathRouteRegistry(
            @Fuzzy ComponentCollection<RouterCustomizer> customizers,
            @LoggerMeta(context = HandlerMappingRegistrar.class) Logger logger
    ) {
        HandlerMappingRegistry registry = new SimpleHandlerMappingRegistry();
        HandlerMappingRegistrar registrar = new SimpleHandlerMappingRegistrar(registry, logger);
        customizers.forEach(customizer -> customizer.configure(registrar));
        return registry;
    }

    @Singleton
    @CompositeMember
    public RouterCustomizer declarativeRouterPathConfigurer(
            ComponentRegistry componentRegistry,
            ConversionService conversionService,
            InjectionCapableApplication application
    ) {
        return new DeclarativeRouterPathConfigurer(
                componentRegistry,
                conversionService,
                application
        );
    }

    @Singleton
    public HandlerMappingResolver handlerMappingResolver(
            PathPatternMatcher patternMatcher,
            HandlerMappingRegistry registry
    ) {
        return new PatternMatchingHandlerMappingResolver(patternMatcher, registry);
    }

    @Singleton
    public PathPatternMatcher pathPatternMatcher() {
        return new StandardPathPatternMatcher();
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
     * Provides the current {@link HttpServletRequest} from the {@link WebRequestScope}.
     *
     * @param scope The web request scope.
     *
     * @return The current web request.
     */
    @Prototype
    @Scoped(WebRequestScope.class)
    public HttpServletRequest webRequest(WebRequestScope scope) {
        return scope.request();
    }

    /**
     * Provides the current {@link HttpServletResponse} from the {@link WebRequestScope}.
     *
     * @param scope The web request scope.
     *
     * @return The current web response.
     */
    @Prototype
    @Scoped(WebRequestScope.class)
    public HttpServletResponse webResponse(WebRequestScope scope) {
        return scope.response();
    }

    /**
     * Creates a {@link WebServerDiagnosticsReporter} for reporting diagnostics related to the web
     * server.
     *
     * @param webServer The web server to report diagnostics for.
     *
     * @return A web server diagnostics reporter.
     */
    @Singleton
    @CompositeMember
    public CategorizedDiagnosticsReporter webServerDiagnosticsReporter(WebServer webServer) {
        return new WebServerDiagnosticsReporter(webServer);
    }

    @Configuration
    @RequiresClass(classes = JsonMapper.class)
    public static class JacksonJsonResponseWriterConfiguration {

        /**
         * Creates a {@link ResponseWriter} that uses a {@link JsonMapper} to serialize objects to
         * JSON.
         *
         * @param jsonMapper The JSON mapper to use for serialization.
         *
         * @return A response writer for JSON objects.
         */
        @Singleton
        @Named("json")
        public ResponseWriter<Object> objectMapperResponseWriter(JsonMapper jsonMapper) {
            return new ObjectMapperResponseWriter(jsonMapper, "application/json");
        }
    }
}
