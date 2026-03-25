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

import org.dockbox.hartshorn.util.stream.EntryStream;
import org.dockbox.hartshorn.web.HttpMethod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A simple implementation of {@link HandlerMappingRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleHandlerMappingRegistry implements HandlerMappingRegistry {

    private final Map<RouteMapping, RequestHandler> mappings = new ConcurrentHashMap<>();

    @Override
    public void add(RouteMapping mapping, RequestHandler handler) {
        this.mappings.put(mapping, handler);
    }

    @Override
    public Map<RouteMapping, RequestHandler> mappings() {
        return this.mappings;
    }

    @Override
    public Map<String, RequestHandler> mappings(HttpMethod method) {
        return EntryStream.of(this.mappings)
                .filterKeys(mapping -> mapping.method() == method)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().pathPattern(),
                        Map.Entry::getValue
                ));
    }
}
