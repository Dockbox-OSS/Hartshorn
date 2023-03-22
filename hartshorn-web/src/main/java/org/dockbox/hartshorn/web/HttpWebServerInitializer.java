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
import org.dockbox.hartshorn.web.annotations.UseHttpServer;
import org.dockbox.hartshorn.web.servlet.HandleWebServlet;
import org.dockbox.hartshorn.web.servlet.HttpWebServletAdapter;
import org.dockbox.hartshorn.web.servlet.WebServlet;
import org.dockbox.hartshorn.web.servlet.WebServletFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.servlet.Servlet;

@Service
@RequiresActivator(UseHttpServer.class)
public class HttpWebServerInitializer implements LifecycleObserver {

    public static final int DEFAULT_PORT = 8080;

    @Value("hartshorn.web.port")
    private final int port = DEFAULT_PORT;

    @Value("hartshorn.web.servlet.directory")
    private final boolean useDirectoryServlet = true;

    @Inject
    private WebServletFactory webServletFactory;

    @Inject
    private HttpWebServer webServer;

    @Inject
    private List<WebContextLoader> contextLoaders;

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        final Map<String, Servlet> servlets = new HashMap<>();

        final ControllerContext controllerContext = applicationContext.first(ControllerContext.class).get();
        for (final RequestHandlerContext context : controllerContext.requestHandlerContexts()) {
            final WebServlet servlet = this.servlet(applicationContext, context, this.webServer);
            final Servlet adapter = new HttpWebServletAdapter(applicationContext, servlet);
            servlets.put(context.pathSpec(), adapter);
        }

        for (final WebContextLoader contextLoader : this.contextLoaders) {
            final Map<String, Servlet> extraServlets = contextLoader.loadServlets(applicationContext, this.webServer);
            servlets.putAll(extraServlets);
        }

        servlets.forEach((path, servlet) -> this.webServer.register(servlet, path));

        applicationContext.log().info("Located and registered " + servlets.size() + " servlet" + (servlets.size() == 1 ? "" : "s") + (this.useDirectoryServlet ? " and will serve static content" : ""));

        this.webServer.listStaticDirectories(this.useDirectoryServlet);

        for (final WebContextLoader contextLoader : this.contextLoaders) {
            contextLoader.initializeContext(applicationContext, this.webServer);
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
        final WebServlet servlet = this.webServletFactory.webServlet(webServer, context);
        if (servlet instanceof HandleWebServlet handleWebServlet) {
            handleWebServlet.handler().mapper().skipBehavior(webServer.skipBehavior());
        }
        return servlet;
    }
}
