package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

public record SimpleRouteMapping(
        HttpMethod method,
        String pathPattern
) implements RouteMapping {
}
