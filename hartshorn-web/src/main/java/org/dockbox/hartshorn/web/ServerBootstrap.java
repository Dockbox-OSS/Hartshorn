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

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.boot.ServerState.Started;
import org.dockbox.hartshorn.boot.annotations.UseBootstrap;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.events.EngineChangedState;
import org.dockbox.hartshorn.events.annotations.Listener;

@Service(activators = UseBootstrap.class)
public class ServerBootstrap {

    public static final int DEFAULT_PORT = 8080;

    @Value(value = "hartshorn.web.port", or = "" + DEFAULT_PORT)
    private int port;

    @Listener
    public void on(final EngineChangedState<Started> event) throws ApplicationException {
        final HttpWebServer starter = event.applicationContext().get(HttpWebServer.class);
        final ControllerContext controllerContext = event.applicationContext().first(ControllerContext.class).get();
        for (final RequestHandlerContext context : controllerContext.contexts()) {
            starter.register(context);
        }
        starter.start(this.port);
    }

}
