package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

public record PathHandlerSpec(
        HttpMethod method,
        RequestHandler handler
) {
}
