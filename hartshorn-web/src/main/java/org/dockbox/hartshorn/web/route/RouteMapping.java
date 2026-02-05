package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

public interface RouteMapping {

    HttpMethod method();

    String pathPattern();

    static RouteMapping of(HttpMethod method, String pathPattern) {
        return new SimpleRouteMapping(method, pathPattern);
    }
}
