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

import org.dockbox.hartshorn.util.collections.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.spec.PathSpec;

import java.util.Collection;

/**
 * A simple implementation of {@link HandlerMappingRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleHandlerMappingRegistry implements HandlerMappingRegistry {

    private final MultiMap<PathSpec, PathHandlerSpec> requestMappings
            = new ConcurrentSetMultiMap<>();

    @Override
    public void add(HttpMethod method, PathSpec pathSpec, RequestHandler handler) {
        Collection<PathHandlerSpec> handlerSpecs = this.requestMappings.get(pathSpec);
        if (handlerSpecs.stream().anyMatch(spec -> spec.method() == method)) {
            throw new IllegalArgumentException(
                    "A handler for path '%s' and method '%s' is already registered".formatted(
                            pathSpec, method
                    )
            );
        }
        this.requestMappings.put(pathSpec, new PathHandlerSpec(method, handler));
    }

    @Override
    public MultiMap<PathSpec, PathHandlerSpec> mappings() {
        return this.requestMappings;
    }
}
