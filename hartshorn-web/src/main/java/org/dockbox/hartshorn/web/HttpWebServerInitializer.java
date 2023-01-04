/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;
import org.dockbox.hartshorn.web.annotations.UseMvcServer;
import org.dockbox.hartshorn.web.mvc.MVCInitializer;
import org.dockbox.hartshorn.web.mvc.ViewTemplate;
import org.dockbox.hartshorn.web.servlet.HttpWebServletAdapter;
import org.dockbox.hartshorn.web.servlet.MvcServlet;
import org.dockbox.hartshorn.web.servlet.WebServlet;
import org.dockbox.hartshorn.web.servlet.WebServletFactory;
import org.dockbox.hartshorn.web.servlet.WebServletImpl;

import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.servlet.Servlet;

@Service
@RequiresActivator(UseHttpServer.class)
public class HttpWebServerInitializer implements LifecycleObserver {

    public static final int DEFAULT_PORT = 8080;

    @Value("hartshorn.web.port")
    private int port = DEFAULT_PORT;

    @Value("hartshorn.web.servlet.directory")
    private boolean useDirectoryServlet = true;

    @Inject
    private WebServletFactory webServletFactory;

    @Inject
    private HttpWebServer webServer;

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        final Map<String, Servlet> servlets = new HashMap<>();

        final ControllerContext controllerContext = applicationContext.first(ControllerContext.class).get();
        for (final RequestHandlerContext context : controllerContext.requestHandlerContexts()) {
            final WebServlet servlet = this.servlet(applicationContext, context, this.webServer);
            final Servlet adapter = new HttpWebServletAdapter(applicationContext, servlet);
            servlets.put(context.pathSpec(), adapter);
        }

        final boolean initializeMvcComponents = applicationContext.hasActivator(UseMvcServer.class);

        if (initializeMvcComponents) {
            final MvcControllerContext mvcControllerContext = applicationContext.first(MvcControllerContext.class).get();
            for (final RequestHandlerContext context : mvcControllerContext.requestHandlerContexts()) {
                final MvcServlet servlet = this.webServletFactory.mvc((MethodView<?, ViewTemplate>) context.method());
                final Servlet adapter = new HttpWebServletAdapter(applicationContext, servlet);
                servlets.put(context.pathSpec(), adapter);
            }
        }

        servlets.forEach((path, servlet) -> this.webServer.register(servlet, path));

        applicationContext.log().info("Located and registered " + servlets.size() + " servlet" + (servlets.size() == 1 ? "" : "s") + (this.useDirectoryServlet ? " and will serve static content" : ""));

        this.webServer.listStaticDirectories(this.useDirectoryServlet);

        if (initializeMvcComponents) {
            try {
                final MVCInitializer initializer = applicationContext.get(MVCInitializer.class);
                initializer.initialize(applicationContext);
            } catch (final ApplicationException e) {
                applicationContext.handle("Failed to initialize MVC components", e);
            }
        }

        try {
            this.webServer.start(this.port);
        }
        catch (final ApplicationException e) {
            applicationContext.handle(e);
        }
    }

    @Override
    public void onExit(final ApplicationContext applicationContext) {
        try {
            this.webServer.stop();
        } catch (final ApplicationException e) {
            applicationContext.handle(e);
        }
    }

    protected WebServlet servlet(final ApplicationContext applicationContext, final RequestHandlerContext context, final HttpWebServer webServer) {
        final WebServletImpl adapter = this.webServletFactory.webServlet(webServer, context);
        adapter.handler().mapper().skipBehavior(webServer.skipBehavior());
        return adapter;
    }
}
