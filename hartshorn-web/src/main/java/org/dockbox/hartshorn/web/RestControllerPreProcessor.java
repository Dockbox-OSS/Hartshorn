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

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServicePreProcessor;
import org.dockbox.hartshorn.web.annotations.RestController;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;

@AutomaticActivation
public class RestControllerPreProcessor implements ServicePreProcessor<UseHttpServer> {
    @Override
    public Class<UseHttpServer> activator() {
        return UseHttpServer.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        final TypeContext<?> type = key.type();
        return type.annotation(RestController.class).present() && !type.methods(HttpRequest.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final ControllerContext controllerContext = context.first(ControllerContext.class).get();
        for (final MethodContext<?, T> method : key.type().methods(HttpRequest.class)) {
            controllerContext.add(new RequestHandlerContext(context, method));
        }
    }
}
