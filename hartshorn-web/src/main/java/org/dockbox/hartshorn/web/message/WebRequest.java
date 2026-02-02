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

import org.dockbox.hartshorn.web.HttpMethod;

/**
 * A representation of a web request, containing information such as the request path, method,
 * headers, query parameters, body, client information, and path parameters.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface WebRequest {

    /**
     * Returns the path of the web request, excluding query parameters.
     *
     * @return The request path.
     */
    String path();

    /**
     * Returns the HTTP method of the web request.
     *
     * @return The HTTP method.
     */
    HttpMethod method();

    /**
     * Returns the headers of the web request.
     *
     * @return The request headers.
     */
    HttpMessageHeaders headers();

    /**
     * Returns the query parameters of the web request.
     *
     * @return The request query parameters.
     */
    HttpMessageQuery query();

    /**
     * Returns the body of the web request.
     *
     * @return The request body.
     */
    HttpMessageBody body();

    /**
     * Returns the client information of the web request.
     *
     * @return The request client information.
     */
    WebRequestClient client();

    /**
     * Returns the path parameters of the web request.
     *
     * @return The request path parameters.
     */
    WebRequestPathParameters pathParameters();
}
