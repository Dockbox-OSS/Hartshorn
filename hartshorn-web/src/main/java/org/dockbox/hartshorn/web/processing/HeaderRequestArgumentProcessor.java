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

package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.web.annotations.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HeaderRequestArgumentProcessor implements RequestArgumentProcessor<RequestHeader> {
    @Override
    public Class<RequestHeader> annotation() {
        return RequestHeader.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final ParameterContext<?> parameter, final HttpServletRequest request, final HttpServletResponse response) {
        return RequestArgumentProcessor.super.preconditions(context, parameter, request, response)
                && (parameter.type().childOf(String.class) || parameter.type().childOf(int.class) || parameter.type().childOf(long.class));
    }

    @Override
    public <T> Exceptional<T> process(final ApplicationContext context, final ParameterContext<T> parameter, final HttpServletRequest request, final HttpServletResponse response) {
        final RequestHeader requestHeader = parameter.annotation(RequestHeader.class).get();

        if (!request.getHeaders(requestHeader.value()).hasMoreElements()) return Exceptional.empty();

        if (parameter.type().is(String.class)) return Exceptional.of(() -> (T) request.getHeader(requestHeader.value()));
        else if (parameter.type().childOf(int.class)) return Exceptional.of(() -> request.getIntHeader(requestHeader.value())).map(v -> (T) v);
        else if (parameter.type().childOf(long.class)) return Exceptional.of(() -> request.getDateHeader(requestHeader.value())).map(v -> (T) v);
        return Exceptional.empty();
    }
}
