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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.web.mvc.ViewModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MvcParameterLoaderContext extends HttpRequestParameterLoaderContext{

    private final ViewModel viewModel;

    public MvcParameterLoaderContext(final MethodContext<?, ?> method, final TypeContext<?> type, final Object instance, final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response,
                                     final ViewModel viewModel) {
        super(method, type, instance, applicationContext, request, response);
        this.viewModel = viewModel;
    }

    public ViewModel viewModel() {
        return this.viewModel;
    }
}
