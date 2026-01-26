package org.dockbox.hartshorn.web.chain;

import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.route.PathRouteRegistry;
import org.dockbox.hartshorn.web.route.RequestHandler;

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
        RequestHandler handler = this.registry.handler(request);
        if (handler != null) {
            handler.handle(request, response);
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
