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

import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A request filter that handles uncaptured requests by returning a 404 Not Found response.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class UncapturedRequestFilter implements RequestFilter {

    @Override
    public void handle(
        WebRequest request,
        WebResponse response,
        RequestFilterChain chain
    ) throws Exception {
        // TODO: Configurable 404 handling
        response.status(HttpStatus.NOT_FOUND.code());
        response.headers().set("Content-Type", "text/html; charset=UTF-8");
        ByteBuffer buffer = ByteBuffer.wrap("""
            <h1>404 Not Found</h1>
            <p>The requested resource was not found on this server.</p>
            """.getBytes(StandardCharsets.UTF_8));
        response.write(buffer);
    }

    @Override
    public int order() {
        return ProcessingPriority.LOWEST_PRECEDENCE;
    }
}
