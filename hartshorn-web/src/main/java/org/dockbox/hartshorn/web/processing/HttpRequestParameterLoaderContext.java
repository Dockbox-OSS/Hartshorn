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

package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpRequestParameterLoaderContext extends ParameterLoaderContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public HttpRequestParameterLoaderContext(final MethodView<?, ?> method, final TypeView<?> type, final Object instance, final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response) {
        super(method, instance, applicationContext);
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest request() {
        return this.request;
    }

    public HttpServletResponse response() {
        return this.response;
    }
}
