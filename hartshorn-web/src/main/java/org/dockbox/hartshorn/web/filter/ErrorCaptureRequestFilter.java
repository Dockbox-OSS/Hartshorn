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

import org.dockbox.hartshorn.inject.ExceptionHandler;
import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A request filter that captures any errors occurring during request processing
 * and handles them by logging and returning a generic error response.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ErrorCaptureRequestFilter implements RequestFilter {

    private final ExceptionHandler exceptionHandler;

    public ErrorCaptureRequestFilter(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public boolean handle(
        WebRequest request,
        WebResponse response,
        RequestFilterChain chain
    ) throws Exception {
        try {
            return chain.accept(request, response);
        }
        catch (Throwable throwable) {
            this.exceptionHandler.handle(
                "An error occurred while processing a web request",
                throwable
            );
            this.handleError(throwable, response);
            return false;
        }
    }

    @Override
    public int order() {
        return ProcessingPriority.HIGHEST_PRECEDENCE;
    }

    private void handleError(Throwable throwable, WebResponse response) throws Exception {
        response.status(HttpStatus.INTERNAL_SERVER_ERROR);
        response.headers().set("Content-Type", "text/html; charset=UTF-8");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        throwable.printStackTrace(writer);
        writer.flush();

        ByteBuffer buffer = ByteBuffer.wrap("""
            <html>
                <head><title>Internal Server Error</title></head>
                <body>
                    <h1>500 - Internal Server Error</h1>
                    <p>An unexpected error occurred while processing your request.</p>
                    <pre>%s</pre>
                </body>
            </html>
            """
            .formatted(out.toString(StandardCharsets.UTF_8))
            .getBytes(StandardCharsets.UTF_8)
        );
        response.write(buffer);
    }
}
