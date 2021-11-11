/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.jetty;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.persistence.properties.PersistenceModifier;
import org.dockbox.hartshorn.web.DefaultHttpWebServer;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

@Binds(HttpWebServer.class)
public class JettyHttpWebServer extends DefaultHttpWebServer {

    @Inject @Getter
    private ApplicationContext context;
    @Inject @Getter
    private final ServletContextHandler handler;
    @Setter
    private PersistenceModifier skipBehavior = PersistenceModifier.SKIP_NONE;

    public JettyHttpWebServer() {
        super();
        this.handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        this.handler.setContextPath("/");
    }

    @Override
    public void start(final int port) throws ApplicationException {
        try {
            final QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setName(Hartshorn.PROJECT_NAME);

            final Server server = new Server(threadPool);
            server.setConnectors(new Connector[]{ this.connector(server, port) });
            server.setHandler(this.handler);
            server.setErrorHandler(this.errorHandler());
            server.start();
        } catch (final Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public JettyHttpWebServer register(final RequestHandlerContext context) {
        this.handler.addServlet(this.createHolder(context), context.pathSpec());
        return this;
    }

    protected Connector connector(final Server server, final int port) {
        final HttpConnectionFactory http11 = new HttpConnectionFactory(this.httpConfig());
        final ServerConnector connector = new ServerConnector(server, 1, 1, http11);
        connector.setHost("0.0.0.0");
        connector.setAcceptQueueSize(128);
        connector.setPort(port);
        return connector;
    }

    protected HttpConfiguration httpConfig() {
        final HttpConfiguration configuration = new HttpConfiguration();
        configuration.setSendServerVersion(false);
        configuration.setSendDateHeader(false);
        return configuration;
    }

    protected ErrorHandler errorHandler() {
        return this.context.get(JettyErrorAdapter.class);
    }

    protected ServletHolder createHolder(final RequestHandlerContext context) {
        return new ServletHolder(this.servlet(context));
    }

    protected JettyServletAdapter servlet(final RequestHandlerContext context) {
        JettyServletAdapter adapter = this.context.get(JettyServletFactory.class).adapter(this, context);
        adapter.handler().mapper().skipBehavior(this.skipBehavior);
        return adapter;
    }

}
