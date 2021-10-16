/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.ParameterContext;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.web.annotations.RequestBody;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BodyRequestArgumentProcessor implements RequestArgumentProcessor<RequestBody> {

    @Override
    public Class<RequestBody> annotation() {
        return RequestBody.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Exceptional<T> process(final ApplicationContext context, final ParameterContext<T> parameter, final HttpServletRequest request, final HttpServletResponse response) {
        final Exceptional<String> body = Exceptional.of(() -> request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        if (parameter.type().is(String.class))
            return (Exceptional<T>) body;
        final FileType bodyFormat = parameter.declaringElement().annotation(HttpRequest.class).get().bodyFormat();
        final ObjectMapper objectMapper = context.get(ObjectMapper.class).fileType(bodyFormat);
        return body.flatMap(b -> objectMapper.read(b, parameter.type()));
    }
}
