package org.dockbox.hartshorn.web.chain;

import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.web.message.PathParamAwareWebRequest;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.route.PathRouteRegistry;
import org.dockbox.hartshorn.web.route.RequestHandler;

import java.util.Map;

public class PathMatchingRequestFilter implements RequestFilter {

    private final PathRouteRegistry registry;

    public PathMatchingRequestFilter(PathRouteRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void handle(
        WebRequest request,
        WebResponse response,
        RequestFilterChain chain
    ) throws Exception {
        PathRouteRegistry.RegisteredRoute route = this.registry.handler(request);
        if (route != null) {
            RequestHandler handler = route.handler();
            Map<String, String> parameters = route.pathParameters();
            PathParamAwareWebRequest wrapper = new PathParamAwareWebRequest(request, parameters);
            handler.handle(wrapper, response);
        }
        else {
            chain.accept(request, response);
        }
    }

    @Override
    public int order() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
