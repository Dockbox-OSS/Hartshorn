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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.web.annotations.RestController;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;

import java.util.List;

public class RestControllerPreProcessor extends ComponentPreProcessor {

    @Override
    public <T> void process(final ApplicationContext context, final ComponentProcessingContext<T> processingContext) {
        final TypeView<T> type = processingContext.type();

        final boolean isRestController = type.annotations().has(RestController.class);
        final List<MethodView<T, ?>> httpRequestHandlers = type.methods().annotatedWith(HttpRequest.class);
        final boolean hasHttpRequestHandlers = !httpRequestHandlers.isEmpty();

        if (isRestController && hasHttpRequestHandlers) {
            final ControllerContext controllerContext = context.first(ControllerContext.class).get();
            for (final MethodView<T, ?> method : httpRequestHandlers) {
                controllerContext.add(new RequestHandlerContext(context, method));
            }
        }
    }
}
