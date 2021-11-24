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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.boot.LifecycleObserver;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;
import org.dockbox.hartshorn.web.annotations.UseMvcServer;
import org.dockbox.hartshorn.web.mvc.MVCInitializer;
import org.dockbox.hartshorn.web.mvc.ViewTemplate;
import org.dockbox.hartshorn.web.servlet.HttpWebServletAdapter;
import org.dockbox.hartshorn.web.servlet.MvcServlet;
import org.dockbox.hartshorn.web.servlet.WebServlet;
import org.dockbox.hartshorn.web.servlet.WebServletFactory;
import org.dockbox.hartshorn.web.servlet.WebServletImpl;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;

@Service(activators = UseHttpServer.class)
public class HttpWebServerInitializer implements LifecycleObserver {

    public static final int DEFAULT_PORT = 8080;

    @Value(value = "hartshorn.web.port", or = "" + DEFAULT_PORT)
    private int port;

    @Value(value = "hartshorn.web.servlet.directory", or = "true")
    private boolean useDirectoryServlet;

    @Inject
    private WebServletFactory webServletFactory;

    @Inject
    private HttpWebServer webServer;

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        final Map<String, Servlet> servlets = HartshornUtils.emptyMap();

        final ControllerContext controllerContext = applicationContext.first(ControllerContext.class).get();
        for (final RequestHandlerContext context : controllerContext.contexts()) {
            final WebServlet servlet = this.servlet(applicationContext, context, this.webServer);
            final Servlet adapter = new HttpWebServletAdapter(applicationContext, servlet);
            servlets.put(context.pathSpec(), adapter);
        }

        if (applicationContext.hasActivator(UseMvcServer.class)) {
            final MvcControllerContext mvcControllerContext = applicationContext.first(MvcControllerContext.class).get();
            for (final RequestHandlerContext context : mvcControllerContext.contexts()) {
                final MvcServlet servlet = this.webServletFactory.mvc((MethodContext<ViewTemplate, ?>) context.methodContext());
                final Servlet adapter = new HttpWebServletAdapter(applicationContext, servlet);
                servlets.put(context.pathSpec(), adapter);
            }
        }

        servlets.forEach((path, servlet) -> this.webServer.register(servlet, path));

        applicationContext.log().info("Located and registered " + servlets.size() + " servlet" + (servlets.size() == 1 ? "" : "s") + (this.useDirectoryServlet ? " and will serve static content" : ""));

        this.webServer.listStaticDirectories(this.useDirectoryServlet);

        try {
            final MVCInitializer initializer = applicationContext.get(MVCInitializer.class);
            initializer.initialize(applicationContext);

            this.webServer.start(this.port);
        }
        catch (final ApplicationException e) {
            throw e.runtime();
        }
    }

    @Override
    public void onExit(final ApplicationContext applicationContext) {
        try {
            this.webServer.stop();
        } catch (final ApplicationException e) {
            Except.handle(e);
        }
    }

    protected WebServlet servlet(final ApplicationContext applicationContext, final RequestHandlerContext context, final HttpWebServer webServer) {
        final WebServletImpl adapter = this.webServletFactory.webServlet(webServer, context);
        adapter.handler().mapper().skipBehavior(webServer.skipBehavior());
        return adapter;
    }
}
