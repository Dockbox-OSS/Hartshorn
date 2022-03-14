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

package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpRequestParameterLoaderContext extends ParameterLoaderContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public HttpRequestParameterLoaderContext(final MethodContext<?, ?> method, final TypeContext<?> type, final Object instance, final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response) {
        super(method, type, instance, applicationContext);
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
