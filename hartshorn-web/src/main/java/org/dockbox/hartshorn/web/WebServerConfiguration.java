package org.dockbox.hartshorn.web;

import java.util.Comparator;
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
import org.dockbox.hartshorn.web.chain.ErrorCaptureHandlerStrategy;
import org.dockbox.hartshorn.web.chain.PathMatchingHandlerStrategy;
import org.dockbox.hartshorn.web.chain.RequestHandlerChain;
import org.dockbox.hartshorn.web.chain.RequestHandlerStrategy;
import org.dockbox.hartshorn.web.chain.SimpleRequestHandlerChain;
import org.dockbox.hartshorn.web.chain.UncapturedRequestHandlerStrategy;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.route.PathMatchingRouterPathConfigurer;
import org.dockbox.hartshorn.web.route.PathRouteRegistry;
import org.dockbox.hartshorn.web.route.RouterPathConfigurer;

@Configuration
@RequiresActivator(UseWebServer.class)
public class WebServerConfiguration {

    @Singleton
    public RequestHandlerChain requestHandlerChain(
        @Fuzzy ComponentCollection<RequestHandlerStrategy> strategies
    ) {
        return new SimpleRequestHandlerChain(strategies.stream()
            .sorted(Comparator.comparingInt(RequestHandlerStrategy::order))
            .toList());
    }

    @Singleton
    @CompositeMember
    public RequestHandlerStrategy errorCaptureHandlerStrategy(ExceptionHandler exceptionHandler) {
        return new ErrorCaptureHandlerStrategy(exceptionHandler);
    }

    @Singleton
    @CompositeMember
    public RequestHandlerStrategy pathMatchingHandlerStrategy(
            PathRouteRegistry registry
    ) {
        return new PathMatchingHandlerStrategy(registry);
    }

    @Singleton
    @CompositeMember
    public RequestHandlerStrategy uncapturedRequestHandlerStrategy() {
        return new UncapturedRequestHandlerStrategy();
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
