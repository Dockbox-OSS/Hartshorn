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

package org.dockbox.hartshorn.web.filter;

import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

/**
 * A chain of request filters that can be used to process web requests and responses.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface RequestFilterChain {

    /**
     * Accepts the given web request and response, passing them to the next filter in the chain.
     *
     * @param request The web request to process.
     * @param response The web response to process.
     *
     * @return true if the request was handled successfully, false otherwise.
     *
     * @throws Exception If an error occurs while processing the request.
     */
    boolean accept(WebRequest request, WebResponse response) throws Exception;
}
