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

package org.dockbox.hartshorn.web.processing.rules;

import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.AnnotatedParameterLoaderRule;
import org.dockbox.hartshorn.web.annotations.RequestHeader;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import javax.servlet.http.HttpServletRequest;

public class HeaderRequestParameterRule extends AnnotatedParameterLoaderRule<RequestHeader, HttpRequestParameterLoaderContext> {

    @Override
    public Class<RequestHeader> annotation() {
        return RequestHeader.class;
    }

    @Override
    public boolean accepts(final ParameterContext<?> parameter, final HttpRequestParameterLoaderContext context, final Object... args) {
        return super.accepts(parameter, context, args) && (parameter.type().childOf(String.class) || parameter.type().childOf(int.class) || parameter.type().childOf(long.class));
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final HttpRequestParameterLoaderContext context, final Object... args) {
        final RequestHeader requestHeader = parameter.annotation(RequestHeader.class).get();

        final HttpServletRequest request = context.request();

        if (!request.getHeaders(requestHeader.value()).hasMoreElements()) return Exceptional.empty();

        if (parameter.type().is(String.class)) return Exceptional.of(() -> (T) request.getHeader(requestHeader.value()));
        else if (parameter.type().childOf(int.class)) return Exceptional.of(() -> request.getIntHeader(requestHeader.value())).map(v -> (T) v);
        else if (parameter.type().childOf(long.class)) return Exceptional.of(() -> request.getDateHeader(requestHeader.value())).map(v -> (T) v);

        return Exceptional.empty();
    }
}
