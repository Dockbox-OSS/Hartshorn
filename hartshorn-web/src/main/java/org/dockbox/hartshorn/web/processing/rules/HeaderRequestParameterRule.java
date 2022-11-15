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

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.parameter.AnnotatedParameterLoaderRule;
import org.dockbox.hartshorn.web.annotations.RequestHeader;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import jakarta.servlet.http.HttpServletRequest;

public class HeaderRequestParameterRule extends AnnotatedParameterLoaderRule<RequestHeader, HttpRequestParameterLoaderContext> {

    @Override
    public Class<RequestHeader> annotation() {
        return RequestHeader.class;
    }

    @Override
    public boolean accepts(final ParameterView<?> parameter, final int index, final HttpRequestParameterLoaderContext context, final Object... args) {
        return super.accepts(parameter, index, context, args) && this.isValidType(parameter.type());
    }

    private boolean isValidType(final TypeView<?> type) {
        return type.isChildOf(String.class)
                || type.isChildOf(int.class)
                || type.isChildOf(long.class);
    }

    @Override
    public <T> Option<T> load(final ParameterView<T> parameter, final int index, final HttpRequestParameterLoaderContext context, final Object... args) {
        final RequestHeader requestHeader = parameter.annotations().get(RequestHeader.class).get();

        final HttpServletRequest request = context.request();

        if (!request.getHeaders(requestHeader.value()).hasMoreElements()) return Option.empty();

        if (parameter.type().is(String.class)) return Option.of(() -> request.getHeader(requestHeader.value())).map(v -> TypeUtils.adjustWildcards(v, Object.class));
        else if (parameter.type().isChildOf(int.class)) return Option.of(() -> request.getIntHeader(requestHeader.value())).map(v -> TypeUtils.adjustWildcards(v, Object.class));
        else if (parameter.type().isChildOf(long.class)) return Option.of(() -> request.getDateHeader(requestHeader.value())).map(v -> TypeUtils.adjustWildcards(v, Object.class));

        return Option.empty();
    }
}
