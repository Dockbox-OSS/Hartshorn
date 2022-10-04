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

import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.parameter.AnnotatedParameterLoaderRule;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.web.annotations.RequestParam;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

public class RequestQueryParameterRule extends AnnotatedParameterLoaderRule<RequestParam, HttpRequestParameterLoaderContext> {

    @Override
    protected Class<RequestParam> annotation() {
        return RequestParam.class;
    }

    @Override
    public <T> Result<T> load(final ParameterView<T> parameter, final int index, final HttpRequestParameterLoaderContext context, final Object... args) {
        return Result.of(() -> {
            final RequestParam requestParam = parameter.annotations().get(RequestParam.class).get();
            String value = context.request().getParameter(requestParam.value());
            if (value == null) value = requestParam.or();

            if (parameter.type().is(String.class)) return (T) value;
            else if (parameter.type().isPrimitive()) {
                return TypeUtils.toPrimitive(parameter.type().type(), value);
            }
            return null;
        });
    }
}
