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

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.AttributeHolder;
import org.dockbox.hartshorn.di.properties.UseFactory;
import org.dockbox.hartshorn.persistence.properties.ModifiersAttribute;
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

@Binds(HttpWebServer.class)
public class JettyHttpWebServer extends DefaultHttpWebServer implements AttributeHolder {

    @Inject @Getter
    private ApplicationContext context;
    @Inject @Getter
    private final ServletContextHandler handler;
    private ModifiersAttribute mappingModifier = ModifiersAttribute.of();

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
    public void register(final RequestHandlerContext context) {
        this.handler.addServlet(this.createHolder(context), context.pathSpec());
    }

    @Override
    public void apply(final Attribute<?> property) {
        if (property instanceof ModifiersAttribute modifier) this.mappingModifier = modifier;
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
        return this.context.get(JettyServletAdapter.class, new UseFactory(this, context), this.mappingModifier);
    }

}
