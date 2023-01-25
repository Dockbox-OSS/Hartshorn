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

package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.WebContextLoader;
import org.dockbox.hartshorn.web.mvc.template.ViewTemplate;
import org.dockbox.hartshorn.web.servlet.HttpWebServletAdapter;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.Servlet;

public class WebMvcContextLoader implements WebContextLoader {

    @Override
    public Map<String, Servlet> loadServlets(final ApplicationContext applicationContext, final HttpWebServer webServer) {
        final Map<String, Servlet> servlets = new HashMap<>();
        final MvcControllerContext mvcControllerContext = applicationContext.first(MvcControllerContext.class).get();
        final WebMvcServletFactory servletFactory = applicationContext.get(WebMvcServletFactory.class);
        for (final RequestHandlerContext context : mvcControllerContext.requestHandlerContexts()) {
            final MvcServlet servlet = servletFactory.mvc((MethodView<?, ViewTemplate>) context.method());
            final Servlet adapter = new HttpWebServletAdapter(applicationContext, servlet);
            servlets.put(context.pathSpec(), adapter);
        }
        return servlets;
    }

    @Override
    public void initializeContext(final ApplicationContext applicationContext, final HttpWebServer webServer) {
        try {
            final MVCInitializer initializer = applicationContext.get(MVCInitializer.class);
            initializer.initialize(applicationContext);
        } catch (final ApplicationException e) {
            applicationContext.handle("Failed to initialize MVC components", e);
        }
    }
}
