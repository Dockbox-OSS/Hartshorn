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

package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.web.rest.HttpRoute;

import java.util.List;
import org.dockbox.hartshorn.web.rest.RestRouter;

/**
 * Registrar for HTTP routes defined on {@link RestRouter router components}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class RestRouterPathRegistrar {

    private final InjectionCapableApplication application;
    private final ConversionService conversionService;
    private final TypeView<?> routerType;

    public RestRouterPathRegistrar(
            InjectionCapableApplication application,
            ConversionService conversionService,
            TypeView<?> routerType
    ) {
        this.application = application;
        this.conversionService = conversionService;
        this.routerType = routerType;
    }

    /**
     * Registers all HTTP routes defined on the router type to the given
     * {@link RouterPathConfigurer path configurer}.
     *
     * @param target the target path configurer
     */
    public void registerPaths(RouterPathConfigurer target) {
        List<? extends MethodView<?, ?>> routeMethods = this.routerType.methods()
                .annotatedWith(HttpRoute.class);
        for (MethodView<?, ?> routeMethod : routeMethods) {
            RequestHandler handler = new RouterMethodRequestHandler<>(
                    this.application,
                    this.conversionService,
                    routeMethod
            );
            HttpRoute httpRoute = routeMethod.annotations().get(HttpRoute.class)
                    .orElseThrow(() -> new IllegalStateException(
                            "Expected method to be annotated with @%s".formatted(
                                    HttpRoute.class.getSimpleName()
                            )
                    ));
            target.request(httpRoute.method(), httpRoute.path(), handler);
        }
    }
}
