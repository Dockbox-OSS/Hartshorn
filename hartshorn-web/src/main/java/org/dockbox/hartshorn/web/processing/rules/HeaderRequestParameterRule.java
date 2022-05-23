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
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.parameter.AnnotatedParameterLoaderRule;
import org.dockbox.hartshorn.web.annotations.RequestHeader;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import javax.servlet.http.HttpServletRequest;

public class HeaderRequestParameterRule extends AnnotatedParameterLoaderRule<RequestHeader, HttpRequestParameterLoaderContext> {

    @Override
    public Class<RequestHeader> annotation() {
        return RequestHeader.class;
    }

    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final HttpRequestParameterLoaderContext context, final Object... args) {
        return super.accepts(parameter, index, context, args) && (parameter.type().childOf(String.class) || parameter.type().childOf(int.class) || parameter.type().childOf(long.class));
    }

    @Override
    public <T> Result<T> load(final ParameterContext<T> parameter, final int index, final HttpRequestParameterLoaderContext context, final Object... args) {
        final RequestHeader requestHeader = parameter.annotation(RequestHeader.class).get();

        final HttpServletRequest request = context.request();

        if (!request.getHeaders(requestHeader.value()).hasMoreElements()) return Result.empty();

        if (parameter.type().is(String.class)) return Result.of(() -> (T) request.getHeader(requestHeader.value()));
        else if (parameter.type().childOf(int.class)) return Result.of(() -> request.getIntHeader(requestHeader.value())).map(v -> (T) v);
        else if (parameter.type().childOf(long.class)) return Result.of(() -> request.getDateHeader(requestHeader.value())).map(v -> (T) v);

        return Result.empty();
    }
}
