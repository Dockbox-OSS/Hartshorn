/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.parameter.AnnotatedParameterLoaderRule;
import org.dockbox.hartshorn.web.MediaType;
import org.dockbox.hartshorn.web.annotations.RequestBody;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import java.io.IOException;
import java.util.stream.Collectors;

public class BodyRequestParameterRule extends AnnotatedParameterLoaderRule<RequestBody, HttpRequestParameterLoaderContext> {

    @Override
    public Class<RequestBody> annotation() {
        return RequestBody.class;
    }

    @Override
    public <T> Attempt<T, Exception> load(final ParameterView<T> parameter, final int index, final HttpRequestParameterLoaderContext context, final Object... args) {
        final Attempt<String, IOException> body = Attempt.of(() -> context.request().getReader().lines().collect(Collectors.joining(System.lineSeparator())), IOException.class);
        if (parameter.type().is(String.class))
            return body
                    .map(o -> (T) o)
                    .mapError(e -> e);
        final MediaType mediaType = parameter.declaredBy().annotations().get(HttpRequest.class).get().consumes();
        if (!mediaType.isSerializable()) return Attempt.empty();
        final FileFormat bodyFormat = mediaType.fileFormat().get();
        final ObjectMapper objectMapper = context.provider().get(ObjectMapper.class).fileType(bodyFormat);

        return body
                .flatMap(b -> objectMapper.read(b, parameter.type().type()))
                .attempt(ObjectMappingException.class)
                .mapError(e -> e);
    }
}
