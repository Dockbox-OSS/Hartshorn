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
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.jetty.error.JettyErrorAdapter;
import org.dockbox.hartshorn.web.DefaultWebStarter;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.WebStarter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.inject.Inject;

@Binds(WebStarter.class)
public class JettyWebStarter extends DefaultWebStarter {

    @Inject
    private ApplicationContext context;
    private final ServletHandler handler;

    public JettyWebStarter() {
        super();
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
        return new JettyServletAdapter(this, context);
    }

}
