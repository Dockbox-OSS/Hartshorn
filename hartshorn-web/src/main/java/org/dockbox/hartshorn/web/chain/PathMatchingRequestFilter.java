/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.chain;

import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.web.message.PathParamAwareWebRequest;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.route.PathRouteRegistry;
import org.dockbox.hartshorn.web.route.RequestHandler;

import java.util.Map;

/**
 * A request filter that delegates execution to {@link RequestHandler request handlers}, which are
 * selected based on path matching using the active {@link PathRouteRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class PathMatchingRequestFilter implements RequestFilter {

    private final PathRouteRegistry registry;

    public PathMatchingRequestFilter(PathRouteRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void handle(
        WebRequest request,
        WebResponse response,
        RequestFilterChain chain
    ) throws Exception {
        PathRouteRegistry.RegisteredRoute route = this.registry.handler(request);
        if (route != null) {
            RequestHandler handler = route.handler();
            Map<String, String> parameters = route.pathParameters();
            WebRequest wrapper = new PathParamAwareWebRequest(request, parameters);
            handler.handle(wrapper, response);
        }
        else {
            chain.accept(request, response);
        }
    }

    @Override
    public int order() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
