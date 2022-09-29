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
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.web.annotations.PathSpec;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;

public class RequestHandlerContext extends DefaultApplicationAwareContext {

    private final MethodView<?, ?> methodContext;
    private final HttpRequest httpRequest;
    private final String pathSpec;

    public RequestHandlerContext(final ApplicationContext applicationContext, final MethodView<?, ?> methodContext) {
        super(applicationContext);
        this.methodContext = methodContext;
        final Result<HttpRequest> request = methodContext.annotations().get(HttpRequest.class);
        if (request.absent()) throw new IllegalArgumentException(methodContext.qualifiedName() + " is not annotated with @Request or an extension of it.");
        this.httpRequest = request.get();

        final Result<PathSpec> annotation = methodContext.declaredBy().annotations().get(PathSpec.class);
        String spec = this.httpRequest().value();
        spec = spec.startsWith("/") ? spec : '/' + spec;

        if (annotation.present()) {
            String root = annotation.get().pathSpec();
            if (root.endsWith("/")) root = root.substring(0, root.length()-1);
            spec = root + spec;
        }

        this.pathSpec = spec.startsWith("/") ? spec : '/' + spec;
    }

    public MethodView<?, ?> method() {
        return this.methodContext;
    }

    public HttpRequest httpRequest() {
        return this.httpRequest;
    }

    public String pathSpec() {
        return this.pathSpec;
    }
}
