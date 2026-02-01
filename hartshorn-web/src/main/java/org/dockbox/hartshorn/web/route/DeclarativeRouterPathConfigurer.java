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
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.web.rest.RestRouter;

/**
 * A {@link RouterPathRegistrar} that registers routes declared in
 * {@link RestRouter router components}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class DeclarativeRouterPathConfigurer implements RouterPathRegistrar {

    private final ComponentRegistry componentRegistry;
    private final ConversionService conversionService;
    private final InjectionCapableApplication application;

    public DeclarativeRouterPathConfigurer(
            ComponentRegistry componentRegistry,
            ConversionService conversionService,
            InjectionCapableApplication application
    ) {
        this.componentRegistry = componentRegistry;
        this.conversionService = conversionService;
        this.application = application;
    }

    @Override
    public void register(RouterPathConfigurer target) {
        for (ComponentContainer<?> container : componentRegistry.containers()) {
            if (container.type().annotations().has(RestRouter.class)) {
                RestRouterPathRegistrar registrar = new RestRouterPathRegistrar(
                        this.application,
                        this.conversionService,
                        container.type()
                );
                registrar.registerPaths(target);
            }
        }
    }
}
