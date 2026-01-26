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
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.chain.ErrorCaptureRequestFilter;
import org.dockbox.hartshorn.web.chain.PathMatchingRequestFilter;
import org.dockbox.hartshorn.web.chain.RequestFilter;
import org.dockbox.hartshorn.web.chain.RequestFilterChain;
import org.dockbox.hartshorn.web.chain.RequestLoggingFilter;
import org.dockbox.hartshorn.web.chain.SimpleRequestFilterChain;
import org.dockbox.hartshorn.web.chain.UncapturedRequestFilter;
import org.dockbox.hartshorn.web.message.ObjectMapperResponseWriter;
import org.dockbox.hartshorn.web.message.ResponseWriter;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.route.PathMatchingRouterPathConfigurer;
import org.dockbox.hartshorn.web.route.PathRouteRegistry;
import org.dockbox.hartshorn.web.route.RouterPathConfigurer;
import org.slf4j.Logger;
import tools.jackson.databind.json.JsonMapper;

import java.util.Comparator;
import java.util.Objects;

@Configuration
@RequiresActivator(UseWebServer.class)
public class WebServerConfiguration {

    @Singleton
    public RequestFilterChain requestHandlerChain(
        @Fuzzy ComponentCollection<RequestFilter> strategies
    ) {
        return new SimpleRequestFilterChain(strategies.stream()
            .sorted(Comparator.comparingInt(RequestFilter::order))
            .toList());
    }

    @Singleton
    @CompositeMember
    public RequestFilter errorCaptureRequestFilter(ExceptionHandler exceptionHandler) {
        return new ErrorCaptureRequestFilter(exceptionHandler);
    }

    @Singleton
    @CompositeMember
    public RequestFilter pathMatchingRequestFilter(
            PathRouteRegistry registry
    ) {
        return new PathMatchingRequestFilter(registry);
    }

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

    @Singleton
    @CompositeMember
    public RequestFilter uncapturedRequestFilter() {
        return new UncapturedRequestFilter();
    }

    @Singleton
    public PathRouteRegistry pathRouteRegistry(
            @Fuzzy ComponentCollection<Customizer<PathRouteRegistry>> customizers
    ) {
        PathRouteRegistry registry = new PathRouteRegistry();
        customizers.forEach(customizer -> customizer.configure(registry));
        return registry;
    }

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

    @Singleton
    @CompositeMember
    public LifecycleObserver webServerBootstrap() {
        return new WebServerBootstrap();
    }

    @Singleton
    public ResponseWriter<Object> objectMapperResponseWriter(JsonMapper jsonMapper) {
        return new ObjectMapperResponseWriter(jsonMapper, "application/json");
    }

    @Prototype
    @Scoped(WebRequestScope.class)
    public WebRequest webRequest(WebRequestScope scope) {
        return scope.request();
    }

    @Prototype
    @Scoped(WebRequestScope.class)
    public WebResponse webResponse(WebRequestScope scope) {
        return scope.response();
    }
}
