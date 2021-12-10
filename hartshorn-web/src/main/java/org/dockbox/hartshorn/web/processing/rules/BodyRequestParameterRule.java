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
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.web.MediaType;
import org.dockbox.hartshorn.web.annotations.RequestBody;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import java.util.stream.Collectors;

public class BodyRequestParameterRule extends AnnotatedParameterLoaderRule<RequestBody, HttpRequestParameterLoaderContext> {

    @Override
    public Class<RequestBody> annotation() {
        return RequestBody.class;
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final int index, final HttpRequestParameterLoaderContext context, final Object... args) {
        final Exceptional<String> body = Exceptional.of(() -> context.request().getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        if (parameter.type().is(String.class))
            return (Exceptional<T>) body;
        final MediaType mediaType = parameter.declaredBy().annotation(HttpRequest.class).get().consumes();
        if (!mediaType.isSerializable()) return Exceptional.empty();
        final FileFormat bodyFormat = mediaType.fileFormat().get();
        final ObjectMapper objectMapper = context.applicationContext().get(ObjectMapper.class).fileType(bodyFormat);

        return body.flatMap(b -> objectMapper.read(b, parameter.type()));
    }
}
