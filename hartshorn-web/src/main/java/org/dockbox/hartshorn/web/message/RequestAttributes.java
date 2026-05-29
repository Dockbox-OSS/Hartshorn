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

package org.dockbox.hartshorn.web.message;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * Utility class describing common attributes of {@link HttpServletRequest servlet requests} within
 * Hartshorn Web.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class RequestAttributes {

    public static final String REQUEST_PATH_PARAMETERS = "hartshorn.web.request.path.parameters";

    /**
     * Collects any path parameters defined for the given request. If the attribute was not set, an
     * empty {@link Map} is returned instead.
     *
     * @param request the request to collect path parameters from
     * @return any defined path parameters, or an empty {@link Map} if none were defined
     */
    public static Map<String, String> pathParameters(HttpServletRequest request) {
        Object attribute = request.getAttribute(REQUEST_PATH_PARAMETERS);
        if (attribute instanceof Map<?, ?> params) {
            @SuppressWarnings("unchecked")
            Map<String, String> stringParams = (Map<String, String>) params;
            return stringParams;
        }
        return Map.of();
    }

    /**
     * Adds the given request parameters to the given request under the
     * {@value #REQUEST_PATH_PARAMETERS} attribute.
     *
     * @param request the request to add the attribute to
     * @param parameters the parameters to set as value for the attribute
     */
    public static void setPathParameters(
            HttpServletRequest request,
            Map<String, String> parameters
    ) {
        request.setAttribute(REQUEST_PATH_PARAMETERS, parameters);
    }
}
