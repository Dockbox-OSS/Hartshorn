package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;

@Service
@RequiresActivator(UseHttpServer.class)
public class StandardServletFactory implements ServletFactory {
    
    @Override
    public ServletHandler servletHandler(final ApplicationContext applicationContext, final HttpWebServer starter,
                                         final HttpMethod httpMethod, final MethodView<?, ?> methodContext) {
        return new ServletHandler(applicationContext, starter, httpMethod, methodContext);
    }
}
