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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.DefaultCarrierContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.web.annotations.PathSpec;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;

import lombok.Getter;

public class RequestHandlerContext extends DefaultCarrierContext {

    @Getter private final MethodContext<?, ?> methodContext;
    @Getter private final HttpRequest httpRequest;
    @Getter private final String pathSpec;

    public RequestHandlerContext(final ApplicationContext applicationContext, final MethodContext<?, ?> methodContext) {
        super(applicationContext);
        this.methodContext = methodContext;
        final Exceptional<HttpRequest> request = methodContext.annotation(HttpRequest.class);
        if (request.absent()) throw new IllegalArgumentException(methodContext.parent().name() + "#" + methodContext.name() + " is not annotated with @Request or an extension of it.");
        this.httpRequest = request.get();

        final Exceptional<PathSpec> annotation = methodContext.parent().annotation(PathSpec.class);
        String spec = this.httpRequest().value();
        spec = spec.startsWith("/") ? spec : '/' + spec;

        if (annotation.present()) {
            String root = annotation.get().pathSpec();
            if (root.endsWith("/")) root = root.substring(0, root.length()-1);
            spec = root + spec;
        }

        this.pathSpec = spec.startsWith("/") ? spec : '/' + spec;
    }
}
