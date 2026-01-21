package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

public class PathMatchingRouterPathConfigurer implements RouterPathConfigurer {

    private final PathRouteRegistry routeRegistry;

    public PathMatchingRouterPathConfigurer(PathRouteRegistry routeRegistry) {
        this.routeRegistry = routeRegistry;
    }

    @Override
    public PathMatchingRouterPathConfigurer request(
            HttpMethod method,
            String path,
            RequestHandler handler
    ) {
        this.routeRegistry.register(method, path, handler);
        return this;
    }
}
