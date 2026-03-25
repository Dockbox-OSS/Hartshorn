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

/**
 * A simple implementation of {@link RouteMapping}.
 *
 * @param method the HTTP method of the route mapping
 * @param pathPattern the path pattern of the route mapping
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public record SimpleRouteMapping(
        HttpMethod method,
        String pathPattern
) implements RouteMapping {
}
