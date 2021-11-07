package org.dockbox.hartshorn.jetty;

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.RequestHandlerContext;

@Service
public interface JettyServletFactory {
    @Factory
    JettyServletAdapter adapter(final HttpWebServer starter, final RequestHandlerContext context);
}
