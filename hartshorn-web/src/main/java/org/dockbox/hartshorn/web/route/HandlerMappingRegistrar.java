package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

public interface HandlerMappingRegistrar {

    HandlerMappingRegistrar add(RouteMapping mapping, RequestHandler handler);

    default HandlerMappingRegistrar get(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.GET, pathPattern), handler);
    }

    default HandlerMappingRegistrar post(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.POST, pathPattern), handler);
    }

    default HandlerMappingRegistrar put(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.PUT, pathPattern), handler);
    }

    default HandlerMappingRegistrar delete(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.DELETE, pathPattern), handler);
    }

    default HandlerMappingRegistrar patch(String pathPattern, RequestHandler handler) {
        return add(RouteMapping.of(HttpMethod.PATCH, pathPattern), handler);
    }
}
