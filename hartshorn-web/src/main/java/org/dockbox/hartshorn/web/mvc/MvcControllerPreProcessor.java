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

package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServicePreProcessor;
import org.dockbox.hartshorn.web.MvcControllerContext;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.annotations.MvcController;
import org.dockbox.hartshorn.web.annotations.UseMvcServer;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;

@AutomaticActivation
public class MvcControllerPreProcessor implements ServicePreProcessor<UseMvcServer> {

    @Override
    public Class<UseMvcServer> activator() {
        return UseMvcServer.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        final TypeContext<?> type = key.type();
        return type.annotation(MvcController.class).present() && !type.methods(HttpRequest.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final MvcControllerContext controllerContext = context.first(MvcControllerContext.class).get();
        for (final MethodContext<?, T> method : key.type().methods(HttpRequest.class)) {
            if (method.returnType().childOf(ViewTemplate.class)) {
                final RequestHandlerContext handlerContext = new RequestHandlerContext(context, method);
                controllerContext.add(handlerContext);
            }
            else {
                throw new IllegalArgumentException("Method " + method.name() + " must return a ViewTemplate");
            }
        }
    }
}
