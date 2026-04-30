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
import org.dockbox.hartshorn.inject.annotations.PropertyValue;
import org.dockbox.hartshorn.inject.annotations.Required;
import org.dockbox.hartshorn.inject.annotations.SupportPriority;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.configuration.Scoped;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.condition.support.RequiresAbsentBinding;
import org.dockbox.hartshorn.inject.condition.support.RequiresClass;
import org.dockbox.hartshorn.inject.condition.support.RequiresProperty;
import org.dockbox.hartshorn.launchpad.annotations.LoggerMeta;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.web.filter.RequestLoggingFilter;
import org.dockbox.hartshorn.web.report.WebServerDiagnosticsReporter;
import org.dockbox.hartshorn.web.response.HttpMessageConverter;
import org.dockbox.hartshorn.web.response.ResponseHandler;
import org.dockbox.hartshorn.web.response.SimpleResponseHandler;
import org.dockbox.hartshorn.web.response.support.JacksonHttpMessageConverter;
import org.dockbox.hartshorn.web.response.support.NoContentMessageConverter;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.RouterCustomizer;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.support.DeclarativeRouterPathConfigurer;
import org.dockbox.hartshorn.web.spec.ParameterPathPartSpec;
import org.dockbox.hartshorn.web.spec.StaticPathPartSpec;
import org.dockbox.hartshorn.web.spec.WildcardPathPartSpec;
import org.dockbox.hartshorn.web.spec.parser.PathParser;
import org.dockbox.hartshorn.web.spec.parser.SimplePathParser;
import org.slf4j.Logger;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;

import java.util.List;
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
    @SupportPriority
    @RequiresAbsentBinding(ServerPortProvider.class)
    public ServerPortProvider serverPortProvider(
            @PropertyValue(name = "hartshorn.web.port", defaultValue = "8080") int port
    ) {
        return () -> port;
    }

    /**
     * Creates a {@link HandlerMappingRegistry} and applies all registered customizers to it.
     *
     * @param customizers The collection of customizers for the handler mapping registry.
     * @return A configured handler mapping registry.
     */
    @Singleton
    @SupportPriority
    public HandlerMappingRegistry pathRouteRegistry(
            @Fuzzy ComponentCollection<RouterCustomizer> customizers
    ) {
        HandlerMappingRegistry registry = new SimpleHandlerMappingRegistry();
        HandlerMappingRegistrar registrar = new SimpleHandlerMappingRegistrar(registry);
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
    @SupportPriority
    public RouterCustomizer declarativeRouterPathConfigurer(
            ComponentRegistry componentRegistry,
            ConversionService conversionService,
            InjectionCapableApplication application,
            ResponseHandler responseHandler,
            PathParser pathParser
    ) {
        return new DeclarativeRouterPathConfigurer(
                componentRegistry,
                conversionService,
                application,
                responseHandler,
                pathParser
        );
    }

    /**
     * Standard response handler driven by configured {@link HttpMessageConverter converters}.
     *
     * @param messageConverters type-constrained message converters
     *
     * @return a new {@link SimpleResponseHandler}
     */
    @Singleton
    @SupportPriority
    public ResponseHandler responseHandler(
            HttpMessageConverter<?> fallbackConverter,
            @Fuzzy ComponentCollection<HttpMessageConverter<?>> messageConverters
    ) {
        return new SimpleResponseHandler(
                fallbackConverter,
                messageConverters.stream().toList()
        );
    }

    @Singleton
    @CompositeMember
    public HttpMessageConverter<?> noContentMessageConverter() {
        return new NoContentMessageConverter();
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
    @SupportPriority
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
    @SupportPriority
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

    @Singleton
    @SupportPriority
    public PathParser pathParser() {
        return new SimplePathParser('/', List.of(
                WildcardPathPartSpec::parse,
                ParameterPathPartSpec::parse,
                StaticPathPartSpec::parse
        ));
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
                name = "hartshorn.web.default-response.format",
                withValue = "jackson-json",
                matchIfMissing = true
        )
        public HttpMessageConverter<?> jacksonResponseHandler(JsonMapper jsonMapper) {
            return new JacksonHttpMessageConverter(jsonMapper, MediaTypes.APPLICATION_JSON);
        }
    }

    /**
     * Jackson-based XML mapping configuration.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    @Configuration
    @RequiresClass(classes = XmlMapper.class)
    public static class JacksonXmlResponseWriterConfiguration {

        /**
         * Creates a {@link HttpMessageConverter} that uses a {@link XmlMapper} to serialize
         * objects to XML.
         *
         * @param xmlMapper The XML mapper to use for serialization.
         *
         * @return A response writer for XML objects.
         */
        @Singleton
        @RequiresProperty(
                name = "hartshorn.web.default-response.format",
                withValue = "jackson-xml",
                matchIfMissing = true
        )
        public HttpMessageConverter<?> jacksonXmlResponseHandler(XmlMapper xmlMapper) {
            return new JacksonHttpMessageConverter(xmlMapper, MediaTypes.APPLICATION_XML);
        }
    }
}
