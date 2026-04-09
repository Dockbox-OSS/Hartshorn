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

import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.slf4j.Logger;

/**
 * A simple implementation of {@link HandlerMappingRegistrar} that is directly related to a given
 * {@link HandlerMappingRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleHandlerMappingRegistrar implements HandlerMappingRegistrar {

    private final HandlerMappingRegistry registry;
    private final Logger logger;

    public SimpleHandlerMappingRegistrar(HandlerMappingRegistry registry, Logger logger) {
        this.registry = registry;
        this.logger = logger;
    }

    @Override
    public HandlerMappingRegistrar add(HttpMethod method, PathSpec pathSpec, RequestHandler handler) {
        logger.info(
                "Registering handler for {} {}: {}",
                method,
                pathSpec.pattern(),
                handler
        );
        this.registry.add(method, pathSpec, handler);
        return this;
    }
}
