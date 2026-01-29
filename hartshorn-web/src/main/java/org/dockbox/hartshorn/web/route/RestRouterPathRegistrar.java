package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.web.rest.HttpRoute;

import java.util.List;

public class RestRouterPathRegistrar {

    private final InjectionCapableApplication application;
    private final ConversionService conversionService;
    private final TypeView<?> routerType;

    public RestRouterPathRegistrar(
            InjectionCapableApplication application,
            ConversionService conversionService,
            TypeView<?> routerType
    ) {
        this.application = application;
        this.conversionService = conversionService;
        this.routerType = routerType;
    }

    public void registerPaths(RouterPathConfigurer target) {
        List<? extends MethodView<?, ?>> routeMethods = this.routerType.methods()
                .annotatedWith(HttpRoute.class);
        for (MethodView<?, ?> routeMethod : routeMethods) {
            RouterMethodRequestHandler<?, ?> handler = new RouterMethodRequestHandler<>(
                    this.application,
                    this.conversionService,
                    routeMethod
            );
            HttpRoute httpRoute = routeMethod.annotations().get(HttpRoute.class)
                    .orElseThrow(() -> new IllegalStateException(
                            "Expected method to be annotated with @%s".formatted(
                                    HttpRoute.class.getSimpleName()
                            )
                    ));
            target.request(httpRoute.method(), httpRoute.path(), handler);
        }
    }
}
