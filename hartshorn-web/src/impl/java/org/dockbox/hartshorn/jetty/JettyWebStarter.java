package org.dockbox.hartshorn.jetty;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.WebStarter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

@Binds(WebStarter.class)
public class JettyWebStarter implements WebStarter {

    private final ServletHandler handler;

    public JettyWebStarter() {
        this.handler = new ServletHandler();
    }

    @Override
    public void start(int port) throws ApplicationException {
        try {
            Server server = new Server(port);
            server.setHandler(this.handler);
            server.start();
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public void register(RequestHandlerContext context) {
        this.handler.addServletWithMapping(this.createHolder(context), context.pathSpec());
    }

    protected ServletHolder createHolder(RequestHandlerContext context) {
        return new ServletHolder(this.servlet(context));
    }

    protected HartshornServlet servlet(RequestHandlerContext context) {
        return new HartshornServlet(context.request().method(), context.methodContext(), context.applicationContext());
    }

}
