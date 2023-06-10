package org.dockbox.hartshorn.web.servlet;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;

@Service
@RequiresActivator(UseHttpServer.class)
public class StandardWebServletFactory implements WebServletFactory {

    @Override
    public WebServlet webServlet(final HttpWebServer starter, final RequestHandlerContext context) {
        return new WebServletImpl(starter, context);
    }
}
