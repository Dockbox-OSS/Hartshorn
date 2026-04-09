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
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.inject.annotations.PropertyValue;
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
import org.dockbox.hartshorn.web.report.WebServerDiagnosticsReporter;
import org.dockbox.hartshorn.web.route.HandlerMapping;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.HandlerMappingResolver;
import org.dockbox.hartshorn.web.route.RouterCustomizer;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.response.GenericHttpMessageConverter;
import org.dockbox.hartshorn.web.route.response.HttpMessageConverter;
import org.dockbox.hartshorn.web.route.response.JacksonHttpMessageConverter;
import org.dockbox.hartshorn.web.route.response.ResponseHandler;
import org.dockbox.hartshorn.web.route.response.SimpleResponseHandler;
import org.dockbox.hartshorn.web.route.support.DeclarativeRouterPathConfigurer;
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
     * Provides a {@link ServerPortProvider} that retrieves the port number from the configuration
     * property {@code hartshorn.web.port}, defaulting to {@code 8080} if the property is not set.
     *
     * @param port The port number to use for the web server, retrieved from configuration.
     *
     * @return A ServerPortProvider that provides the configured port number.
     */
    @Singleton
    @Priority(Priority.SUPPORT_PRIORITY)
    public ServerPortProvider serverPortProvider(
            @PropertyValue(name = "hartshorn.web.port", defaultValue = "8080") int port
    ) {
        return () -> port;
    }

    /**
     * Creates a {@link HandlerMappingRegistry} and applies all registered customizers to it.
     *
     * @param customizers The collection of customizers for the handler mapping registry.
     * @param logger The logger to use for the handler mapping registrar.
     *
     * @return A configured handler mapping registry.
     */
    @Singleton
    @Priority(Priority.SUPPORT_PRIORITY)
    public HandlerMappingRegistry pathRouteRegistry(
            @Fuzzy ComponentCollection<RouterCustomizer> customizers,
            @LoggerMeta(context = HandlerMappingRegistrar.class) Logger logger
    ) {
        HandlerMappingRegistry registry = new SimpleHandlerMappingRegistry();
        HandlerMappingRegistrar registrar = new SimpleHandlerMappingRegistrar(registry, logger);
        customizers.forEach(customizer -> customizer.configure(registrar));
        return registry;
    }

    /**
     * Customizer to capture and register all {@link HttpRoute HTTP routes}.
     *
     * @param componentRegistry the component registry to use for component discovery
     * @param conversionService the conversion service to use for parameter transformation
     * @param application the current application
     * @param responseHandler the handler for handler responses
     *
     * @return a new {@link DeclarativeRouterPathConfigurer}
     *
     * @see DeclarativeRouterPathConfigurer
     */
    @Singleton
    @CompositeMember
    @Priority(Priority.SUPPORT_PRIORITY)
    public RouterCustomizer declarativeRouterPathConfigurer(
            ComponentRegistry componentRegistry,
            ConversionService conversionService,
            InjectionCapableApplication application,
            ResponseHandler responseHandler
    ) {
        return new DeclarativeRouterPathConfigurer(
                componentRegistry,
                conversionService,
                application,
                responseHandler
        );
    }

    /**
     * Standard response handler driven by configured {@link HttpMessageConverter converters}, with
     * a default {@link GenericHttpMessageConverter} as fallback converter.
     *
     * @param httpMessageConverter the default fallback converter
     * @param messageConverters type-constrained message converters
     *
     * @return a new {@link SimpleResponseHandler}
     */
    @Singleton
    @Priority(Priority.SUPPORT_PRIORITY)
    public ResponseHandler responseHandler(
            GenericHttpMessageConverter httpMessageConverter,
            @Fuzzy ComponentCollection<HttpMessageConverter<?>> messageConverters
    ) {
        return new SimpleResponseHandler(
                httpMessageConverter,
                messageConverters.stream().toList()
        );
    }

    /**
     * Pattern-matching capable {@link HandlerMappingResolver}, using the configured
     * {@link PathPatternMatcher} to resolve handler mappings from the configured
     * {@link HandlerMappingRegistry}.
     *
     * @param patternMatcher the pattern matcher to use for routing paths
     * @param registry the registry containing {@link HandlerMapping handler mappings}
     *
     * @return a new {@link PatternMatchingHandlerMappingResolver}
     */
    @Singleton
    @Priority(Priority.SUPPORT_PRIORITY)
    public HandlerMappingResolver handlerMappingResolver(
            PathPatternMatcher patternMatcher,
            HandlerMappingRegistry registry
    ) {
        return new PatternMatchingHandlerMappingResolver(patternMatcher, registry);
    }

    /**
     * Standard implementation of {@link PathPatternMatcher} for matching request paths to route
     * patterns.
     *
     * @return a new {@link StandardPathPatternMatcher}.
     *
     * @see StandardPathPatternMatcher
     */
    @Singleton
    @Priority(Priority.SUPPORT_PRIORITY)
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
    @Priority(Priority.SUPPORT_PRIORITY)
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
    @Priority(Priority.SUPPORT_PRIORITY)
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

    /**
     * Jackson-based JSON mapping configuration.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    @Configuration
    @RequiresClass(classes = JsonMapper.class)
    public static class JacksonJsonResponseWriterConfiguration {

        /**
         * Creates a {@link HttpMessageConverter} that uses a {@link JsonMapper} to serialize
         * objects to JSON.
         *
         * @param jsonMapper The JSON mapper to use for serialization.
         *
         * @return A response writer for JSON objects.
         */
        @Singleton
        @RequiresProperty(
                name = "hartshorn.web.response.json.enabled",
                withValue = "true",
                matchIfMissing = true
        )
        @Priority(Priority.SUPPORT_PRIORITY)
        public GenericHttpMessageConverter jacksonResponseHandler(JsonMapper jsonMapper) {
            return new JacksonHttpMessageConverter(jsonMapper, MediaTypes.APPLICATION_JSON);
        }
    }
}
