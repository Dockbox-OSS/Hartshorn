package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.context.element.MethodContext;

@Service
public interface ServletFactory {
    @Factory
    ServletHandler servletHandler(final HttpWebServer starter, final HttpMethod httpMethod, final MethodContext<?, ?> methodContext);
}
