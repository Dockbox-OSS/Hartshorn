package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.message.WebRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PathRouteRegistry {

    private final Map<
            HttpMethod,
            Map<String, RequestHandler>
            > methodRoutes = new ConcurrentHashMap<>();

    public void register(HttpMethod method, String path, RequestHandler handler) {
        this.methodRoutes
                .computeIfAbsent(method, _ -> new ConcurrentHashMap<>())
                .put(path, handler);
    }

    public RegisteredRoute handler(WebRequest request) {
        Map<String, RequestHandler> routes = this.methodRoutes.get(request.method());
        if (routes == null) {
            return null;
        }
        for (Map.Entry<String, RequestHandler> entry : routes.entrySet()) {
            PathPatternMatcher.MatchResult result = PathPatternMatcher.match(
                    entry.getKey(),
                    request.path()
            );
            if (result.matches()) {
                RequestHandler handler = entry.getValue();
                return new RegisteredRoute(
                        request.method(),
                        entry.getKey(),
                        handler,
                        result.parameters()
                );
            }
        }
        return null;
    }

    public record RegisteredRoute(
            HttpMethod method,
            String path,
            RequestHandler handler,
            Map<String, String> pathParameters
    ) {}
}
