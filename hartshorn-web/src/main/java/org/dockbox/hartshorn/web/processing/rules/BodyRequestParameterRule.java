/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.web.processing.rules;

import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.parameter.AnnotatedParameterLoaderRule;
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
        final ObjectMapper objectMapper = context.provider().get(ObjectMapper.class).fileType(bodyFormat);

        return body.flatMap(b -> objectMapper.read(b, parameter.type()));
    }
}
