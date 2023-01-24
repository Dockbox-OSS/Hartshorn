package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.WebContextLoader;
import org.dockbox.hartshorn.web.mvc.template.ViewTemplate;
import org.dockbox.hartshorn.web.servlet.HttpWebServletAdapter;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.Servlet;

public class WebMvcContextLoader implements WebContextLoader {

    @Override
    public Map<String, Servlet> loadServlets(final ApplicationContext applicationContext, final HttpWebServer webServer) {
        final Map<String, Servlet> servlets = new HashMap<>();
        final MvcControllerContext mvcControllerContext = applicationContext.first(MvcControllerContext.class).get();
        final WebMvcServletFactory servletFactory = applicationContext.get(WebMvcServletFactory.class);
        for (final RequestHandlerContext context : mvcControllerContext.requestHandlerContexts()) {
            final MvcServlet servlet = servletFactory.mvc((MethodView<?, ViewTemplate>) context.method());
            final Servlet adapter = new HttpWebServletAdapter(applicationContext, servlet);
            servlets.put(context.pathSpec(), adapter);
        }
        return servlets;
    }

    @Override
    public void initializeContext(final ApplicationContext applicationContext, final HttpWebServer webServer) {
        try {
            final MVCInitializer initializer = applicationContext.get(MVCInitializer.class);
            initializer.initialize(applicationContext);
        } catch (final ApplicationException e) {
            applicationContext.handle("Failed to initialize MVC components", e);
        }
    }
}
