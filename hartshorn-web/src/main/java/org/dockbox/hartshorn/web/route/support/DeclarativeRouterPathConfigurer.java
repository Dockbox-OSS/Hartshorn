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
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.HttpRoute;
import org.dockbox.hartshorn.web.Router;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.RequestHandler;
import org.dockbox.hartshorn.web.route.RouterCustomizer;
import org.dockbox.hartshorn.web.response.ResponseHandler;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.dockbox.hartshorn.web.spec.parser.PathParser;

import java.util.List;
import java.util.function.Predicate;

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
    private final PathParser pathParser;

    public DeclarativeRouterPathConfigurer(
            ComponentRegistry componentRegistry,
            ConversionService conversionService,
            InjectionCapableApplication application,
            ResponseHandler responseHandler,
            PathParser pathParser
    ) {
        this.componentRegistry = componentRegistry;
        this.conversionService = conversionService;
        this.application = application;
        this.responseHandler = responseHandler;
        this.pathParser = pathParser;
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
            Option<PathSpec> routePathSpec = Option.of(httpRoute.path())
                    .filter(Predicate.not(String::isBlank))
                    .map(pathParser::parse);

            Option<PathSpec> routerPathSpec = routeMethod.declaredBy().annotations()
                    .get(Router.class)
                    .map(Router::value)
                    .filter(Predicate.not(String::isBlank))
                    .map(pathParser::parse);

            PathSpec pathSpec = routePathSpec
                    .map(spec -> routerPathSpec
                            .map(router -> router.combineWith(spec))
                            .orElse(spec)
                    )
                    .orComputeFlat(() -> routerPathSpec)
                    .orElseThrow(() -> new IllegalStateException(
                            "Expected either the router or the route to declare a path"
                    ));
            routes.add(httpRoute.method(), pathSpec, handler);
        }
    }
}
