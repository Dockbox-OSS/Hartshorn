package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.message.WebRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PathRouteRegistry {

    private final Map<String, RequestHandler> routes = new ConcurrentHashMap<>();

    public void register(HttpMethod method, String path, RequestHandler handler) {
        this.routes.put(path, handler);
    }

    public RequestHandler handler(WebRequest request) {
        // TODO: Actual path matching
        // TODO: Include HTTP method
        return this.routes.get(request.path());
    }
}
