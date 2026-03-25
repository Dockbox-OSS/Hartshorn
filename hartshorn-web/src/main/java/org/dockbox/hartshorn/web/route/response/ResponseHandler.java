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

package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletResponse;

/**
 * A {@link ResponseHandler} is responsible for translating the Object response of a request
 * handler. The response handler is responsible for writing the response, as well as setting any
 * necessary headers and status codes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ResponseHandler {

    /**
     * Handles the given result and writes it to the response. The implementation is responsible for
     * setting the appropriate content type and status code, if necessary.
     *
     * @param response the response to write the result to
     * @param result the result to write to the response
     * @throws Exception if an error occurs while writing the result to the response
     */
    void handleResponse(HttpServletResponse response, Object result) throws Exception;
}
