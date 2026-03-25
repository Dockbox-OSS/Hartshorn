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

import jakarta.servlet.http.HttpServletRequest;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.HttpMethod;

/**
 * Resolver to look up {@link HandlerMapping handler mappings}, typically from a
 * {@link HandlerMappingRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HandlerMappingResolver {

    /**
     * Resolves a handler mapping for the given HTTP method and path. The implementation may use
     * the request to resolve the handler mapping, typically only using the {@link HttpMethod} and
     * path, but it is not limited to these. If no handler mapping can be resolved, an empty
     * {@link Option} should be returned.
     *
     * @param method the HTTP method of the request
     * @param path the path of the request
     * @param request the HTTP request, which may be used to resolve the handler mapping
     *
     * @return an {@link Option} containing the resolved handler mapping, or an empty {@link Option}
     * if no handler mapping could be resolved
     */
    Option<HandlerMapping> resolve(HttpMethod method, String path, HttpServletRequest request);
}
