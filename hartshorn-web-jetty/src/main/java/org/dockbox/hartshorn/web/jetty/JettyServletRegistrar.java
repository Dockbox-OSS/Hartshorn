/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.jetty;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import org.dockbox.hartshorn.web.ServletRegistrar;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;

/**
 * An adapter that allows a {@link ServletRegistrar} to be used with Jetty's
 * {@link ServletContextHandler}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JettyServletRegistrar implements ServletRegistrar {

    private final ServletContextHandler handler;
    private final JettyPathSpecTransformer transformer;

    public JettyServletRegistrar(
            ServletContextHandler handler,
            JettyPathSpecTransformer transformer
    ) {
        this.handler = handler;
        this.transformer = transformer;
    }

    @Override
    public void register(PathSpec pathSpec, Servlet servlet) {
        String jettyPathSpec = this.transformer.toJettyPathSpec(pathSpec);
        switch (servlet) {
            case HttpServlet httpServlet ->
                    this.handler.addServlet(httpServlet, jettyPathSpec);
            case ServletHolder servletHolder ->
                    this.handler.addServlet(servletHolder, jettyPathSpec);
            case null -> throw new IllegalArgumentException("Servlet cannot be null");
            default -> {
                ServletHolder servletHolder = new ServletHolder(servlet);
                this.handler.addServlet(servletHolder, jettyPathSpec);
            }
        }
    }
}
