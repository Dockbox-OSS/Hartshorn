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

package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.persistence.properties.PersistenceModifier;
import org.dockbox.hartshorn.web.DefaultHttpWebServer;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.mvc.ViewTemplate;
import org.dockbox.hartshorn.web.servlet.HttpWebServletAdapter;
import org.dockbox.hartshorn.web.servlet.MvcServlet;
import org.dockbox.hartshorn.web.servlet.WebServlet;
import org.dockbox.hartshorn.web.servlet.WebServletFactory;
import org.dockbox.hartshorn.web.servlet.WebServletImpl;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

@Binds(HttpWebServer.class)
public class JettyHttpWebServer extends DefaultHttpWebServer {

    @Inject @Getter
    private ApplicationContext context;
    @Getter
    private final ServletContextHandler handler;
    @Getter
    private final ResourceHandler resourceHandler;
    @Setter
    private PersistenceModifier skipBehavior = PersistenceModifier.SKIP_NONE;
    private Server server;

    @Inject
    public JettyHttpWebServer(final JettyResourceService resourceService) {
        super();
        this.handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        this.handler.setContextPath("/");
        this.resourceHandler = new ResourceHandler(resourceService);
        this.resourceHandler.setHandler(this.handler);
        this.staticContent(Hartshorn.class.getClassLoader().getResource(HttpWebServer.STATIC_CONTENT.substring(1)));
        this.listStaticDirectories(true);
    }

    @Override
    public void start(final int port) throws ApplicationException {
        try {
            if (this.server != null)
                this.server.stop();

            final QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setName(Hartshorn.PROJECT_NAME);

            this.server = new Server(threadPool);
            this.server.setConnectors(new Connector[]{ this.connector(this.server, port) });
            this.server.setHandler(this.resourceHandler);
            this.server.setErrorHandler(this.errorHandler());
            this.server.start();
        } catch (final Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public JettyHttpWebServer register(final RequestHandlerContext context) {
        final WebServlet servlet = this.servlet(context);
        final HttpWebServletAdapter adapter = new HttpWebServletAdapter(servlet);
        this.handler.addServlet(new ServletHolder(adapter), context.pathSpec());
        return this;
    }

    @Override
    public HttpWebServer registerMvc(final RequestHandlerContext context) {
        final MvcServlet servlet = this.context.get(WebServletFactory.class).mvc((MethodContext<ViewTemplate, ?>) context.methodContext());
        final HttpWebServletAdapter adapter = new HttpWebServletAdapter(servlet);
        this.handler.addServlet(new ServletHolder(adapter), context.pathSpec());
        return this;
    }

    @Override
    public HttpWebServer listStaticDirectories(final boolean listDirectories) {
        this.resourceHandler.setDirectoriesListed(listDirectories);
        return this;
    }

    @Override
    public HttpWebServer staticContent(final URI location) {
        try {
            return this.staticContent(location.toURL());
        }
        catch (final MalformedURLException e) {
            Except.handle(e);
        }
        return this;
    }

    public HttpWebServer staticContent(final URL location) {
        this.resourceHandler.setResourceBase(location.toExternalForm());
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
        return this.context.get(JettyErrorHandler.class);
    }

    protected WebServlet servlet(final RequestHandlerContext context) {
        final WebServletImpl adapter = this.context.get(WebServletFactory.class).webServlet(this, context);
        adapter.handler().mapper().skipBehavior(this.skipBehavior);
        return adapter;
    }
}
