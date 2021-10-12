package org.dockbox.hartshorn.jetty;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.jetty.error.JettyErrorAdapter;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.WebStarter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.inject.Inject;

@Binds(WebStarter.class)
public class JettyWebStarter implements WebStarter {

    @Inject
    private ApplicationContext context;

    private final ServletHandler handler;

    public JettyWebStarter() {
        this.handler = new ServletHandler();
    }

    @Override
    public void start(final int port) throws ApplicationException {
        try {
            final Server server = new Server(port);
            server.setHandler(this.handler);
            server.setErrorHandler(this.errorHandler());
            server.start();
        } catch (final Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public void register(final RequestHandlerContext context) {
        this.handler.addServletWithMapping(this.createHolder(context), context.pathSpec());
    }

    protected ErrorHandler errorHandler() {
        return this.context.get(JettyErrorAdapter.class);
    }

    protected ServletHolder createHolder(final RequestHandlerContext context) {
        return new ServletHolder(this.servlet(context));
    }

    protected JettyServletAdapter servlet(final RequestHandlerContext context) {
        return new JettyServletAdapter(context.httpRequest().method(), context.methodContext(), context.applicationContext());
    }

}
