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

package org.dockbox.hartshorn.web.route.support;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.web.HttpRoute;
import org.dockbox.hartshorn.web.Router;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.RequestHandler;
import org.dockbox.hartshorn.web.route.RouteMapping;
import org.dockbox.hartshorn.web.route.RouterCustomizer;
import org.dockbox.hartshorn.web.route.response.ResponseHandler;
import org.dockbox.hartshorn.web.util.RouterUtilities;

import java.util.List;

/**
 * A {@link RouterCustomizer} that registers routes declared in {@link Router router components}
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class DeclarativeRouterPathConfigurer implements RouterCustomizer {

    private final ComponentRegistry componentRegistry;
    private final ConversionService conversionService;
    private final InjectionCapableApplication application;
    private final ResponseHandler responseHandler;

    public DeclarativeRouterPathConfigurer(
            ComponentRegistry componentRegistry,
            ConversionService conversionService,
            InjectionCapableApplication application,
            ResponseHandler responseHandler
    ) {
        this.componentRegistry = componentRegistry;
        this.conversionService = conversionService;
        this.application = application;
        this.responseHandler = responseHandler;
    }

    @Override
    public void configure(HandlerMappingRegistrar routes) {
        for (ComponentContainer<?> container : componentRegistry.containers()) {
            if (container.type().annotations().has(Router.class)) {
                this.register(container, routes);
            }
        }
    }

    private void register(ComponentContainer<?> container, HandlerMappingRegistrar routes) {
        List<? extends MethodView<?, ?>> routeMethods = container.type().methods()
                .annotatedWith(HttpRoute.class);
        for (MethodView<?, ?> routeMethod : routeMethods) {
            RequestHandler handler = new RouterMethodRequestHandler<>(
                    this.application,
                    this.conversionService,
                    this.responseHandler,
                    routeMethod
            );
            HttpRoute httpRoute = routeMethod.annotations().get(HttpRoute.class)
                    .orElseThrow(() -> new IllegalStateException(
                            "Expected method to be annotated with @%s".formatted(
                                    HttpRoute.class.getSimpleName()
                            )
                    ));
            String pathPrefix = routeMethod.declaredBy().annotations().get(Router.class)
                    .map(Router::value)
                    .orElse("");
            String path = RouterUtilities.combinePaths(pathPrefix, httpRoute.path());
            routes.add(RouteMapping.of(httpRoute.method(), path), handler);
        }
    }
}
