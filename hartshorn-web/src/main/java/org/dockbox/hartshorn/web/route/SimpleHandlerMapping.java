package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

public record SimpleHandlerMapping(
        HttpMethod method,
        String pathPattern,
        RequestHandler handler
) implements HandlerMapping {
}
