package org.dockbox.hartshorn.web.jetty;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import org.dockbox.hartshorn.web.ServletRegistrar;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;

public class JettyServletRegistrar implements ServletRegistrar {

    private final ServletContextHandler handler;
    private final JettyPathSpecTransformer transformer;

    public JettyServletRegistrar(
            ServletContextHandler handler,
            JettyPathSpecTransformer transformer
    ) {
        this.handler = handler;
        this.transformer = transformer;
    }

    @Override
    public void register(PathSpec pathSpec, Servlet servlet) {
        String jettyPathSpec = this.transformer.toJettyPathSpec(pathSpec);
        switch (servlet) {
            case HttpServlet httpServlet ->
                    this.handler.addServlet(httpServlet, jettyPathSpec);
            case ServletHolder servletHolder ->
                    this.handler.addServlet(servletHolder, jettyPathSpec);
            case null, default -> {
                ServletHolder servletHolder = new ServletHolder(servlet);
                this.handler.addServlet(servletHolder, jettyPathSpec);
            }
        }
    }
}
