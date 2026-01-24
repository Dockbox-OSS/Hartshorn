package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.inject.ExceptionHandler;
import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.Fuzzy;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.configuration.Scoped;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.chain.ErrorCaptureFilter;
import org.dockbox.hartshorn.web.chain.PathMatchingFilter;
import org.dockbox.hartshorn.web.chain.RequestFilter;
import org.dockbox.hartshorn.web.chain.RequestFilterChain;
import org.dockbox.hartshorn.web.chain.SimpleRequestFilterChain;
import org.dockbox.hartshorn.web.chain.UncapturedRequestFilter;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.route.PathMatchingRouterPathConfigurer;
import org.dockbox.hartshorn.web.route.PathRouteRegistry;
import org.dockbox.hartshorn.web.route.RouterPathConfigurer;

import java.util.Comparator;

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
    public RequestFilter errorCaptureHandlerStrategy(ExceptionHandler exceptionHandler) {
        return new ErrorCaptureFilter(exceptionHandler);
    }

    @Singleton
    @CompositeMember
    public RequestFilter pathMatchingHandlerStrategy(
            PathRouteRegistry registry
    ) {
        return new PathMatchingFilter(registry);
    }

    @Singleton
    @CompositeMember
    public RequestFilter uncapturedRequestHandlerStrategy() {
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
