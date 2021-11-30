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
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.data.mapping.JsonInclusionRule;
import org.dockbox.hartshorn.web.DefaultHttpWebServer;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.inject.Inject;
import javax.servlet.Servlet;

import lombok.Getter;
import lombok.Setter;

@Binds(HttpWebServer.class)
public class JettyHttpWebServer extends DefaultHttpWebServer {

    @Inject @Getter
    private ApplicationContext context;
    @Getter
    private final ServletContextHandler contextHandler;
    @Getter
    private final HandlerWrapper servletHandler;
    @Getter @Setter
    private JsonInclusionRule skipBehavior = JsonInclusionRule.SKIP_NONE;
    private JettyServer server;

    @Inject
    public JettyHttpWebServer(final JettyResourceService resourceService) {
        super();
        this.contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        this.contextHandler.setContextPath("/");
        final URL staticContent = Hartshorn.class.getClassLoader().getResource(HttpWebServer.STATIC_CONTENT.substring(1));
        if (staticContent == null) {
            this.servletHandler = this.contextHandler;
        } else {
            this.servletHandler = new ResourceHandler(resourceService);
            this.servletHandler.setHandler(this.contextHandler);
            this.staticContent(staticContent);
        }
        this.listStaticDirectories(true);
    }

    @Override
    public void start(final int port) throws ApplicationException {
        try {
            if (this.server != null)
                this.server.stop();

            this.context.log().info("Starting service [JettyServer]");
            this.server = this.context.get(JettyServer.class);
            this.server.setConnectors(new Connector[]{ this.connector(this.server, port) });
            this.server.setHandler(this.servletHandler);
            this.server.setErrorHandler(this.errorHandler());
            this.server.start();
            this.context.log().info("Service [JettyServer] started on port [{}]", port);
        } catch (final Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public HttpWebServer register(final Servlet servlet, final String pathSpec) {
        this.contextHandler.addServlet(new ServletHolder(servlet), pathSpec);
        return this;
    }

    @Override
    public HttpWebServer listStaticDirectories(final boolean listDirectories) {
        if (this.servletHandler instanceof ResourceHandler resourceHandler) {
            resourceHandler.setDirectoriesListed(listDirectories);
        }
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

    @Override
    public void stop() throws ApplicationException {
        if (this.server != null) {
            try {
                this.server.stop();
            } catch (final Exception e) {
                throw new ApplicationException(e);
            }
        }
    }

    public HttpWebServer staticContent(final URL location) {
        if (this.servletHandler instanceof ResourceHandler resourceHandler) {
            resourceHandler.setResourceBase(location.toExternalForm());
        }
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
}
