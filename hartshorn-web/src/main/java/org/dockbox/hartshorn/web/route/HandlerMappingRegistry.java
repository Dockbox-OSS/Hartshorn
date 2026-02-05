package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

import java.util.Map;

public interface HandlerMappingRegistry {

    void add(RouteMapping mapping, RequestHandler handler);

    Map<RouteMapping, RequestHandler> mappings();

    Map<String, RequestHandler> mappings(HttpMethod method);
}
