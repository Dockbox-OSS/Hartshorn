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
import org.dockbox.hartshorn.core.annotations.UseBootstrap;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.boot.LifecycleObserver;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.mvc.MVCInitializer;

@Service(activators = UseBootstrap.class)
public class ServerBootstrap implements LifecycleObserver {

    public static final int DEFAULT_PORT = 8080;

    @Value(value = "hartshorn.web.port", or = "" + DEFAULT_PORT)
    private int port;

    @Override
    public void onCreated(final ApplicationContext applicationContext) {
        // Nothing happens
    }

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        final HttpWebServer starter = applicationContext.get(HttpWebServer.class);

        final ControllerContext controllerContext = applicationContext.first(ControllerContext.class).get();
        for (final RequestHandlerContext context : controllerContext.contexts())
            starter.register(context);

        final MvcControllerContext mvcControllerContext = applicationContext.first(MvcControllerContext.class).get();
        for (final RequestHandlerContext context : mvcControllerContext.contexts())
            starter.registerMvc(context);

        try {
            final MVCInitializer initializer = applicationContext.get(MVCInitializer.class);
            initializer.initialize(applicationContext);

            starter.start(this.port);
        }
        catch (final ApplicationException e) {
            throw e.runtime();
        }
    }
}
