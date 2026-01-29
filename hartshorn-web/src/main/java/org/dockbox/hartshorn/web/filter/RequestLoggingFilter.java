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
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.slf4j.Logger;

/**
 * A request filter that logs incoming requests and outgoing responses.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class RequestLoggingFilter implements RequestFilter, Reportable {

    private final boolean includeClientInfo;
    private final boolean includeQueryString;
    private final boolean includeContentType;
    private final boolean includeResponse;
    private final boolean includeDuration;
    private final Logger logger;

    private RequestLoggingFilter(
            boolean includeClientInfo,
            boolean includeQueryString,
            boolean includeContentType,
            boolean includeResponse,
            boolean includeDuration,
            Logger logger
    ) {
        this.includeClientInfo = includeClientInfo;
        this.includeQueryString = includeQueryString;
        this.includeContentType = includeContentType;
        this.includeResponse = includeResponse;
        this.includeDuration = includeDuration;
        this.logger = logger;
    }

    @Override
    public void handle(
            WebRequest request,
            WebResponse response,
            RequestFilterChain chain
    ) throws Exception {
        this.logInboundRequest(request);
        long startTime = System.nanoTime();
        chain.accept(request, response);
        long duration = System.nanoTime() - startTime;
        if (this.includeResponse) {
            this.logOutboundRequest(request, response, duration);
        }
    }

    private void logInboundRequest(WebRequest request) {
        StringBuilder logMessage = new StringBuilder("Incoming request: ")
                .append(request.method())
                .append(" ")
                .append(request.path());

        if (this.includeQueryString && request.query().notEmpty()) {
            logMessage.append("?").append(request.query().asString());
        }

        if (this.includeContentType) {
            request.headers().get("Content-Type").peek(contentType ->
                logMessage.append(" [Content-Type: ").append(contentType).append("]")
            );
        }

        if (this.includeClientInfo) {
            logMessage.append(" from ").append(request.client().address())
                      .append(":").append(request.client().port());
        }

        this.logger.info(logMessage.toString());
    }

    private void logOutboundRequest(WebRequest request, WebResponse response, long duration) {
        StringBuilder logMessage = new StringBuilder("Outgoing response: ")
                .append(request.method())
                .append(" ")
                .append(request.path())
                .append(" -> ")
                .append(response.statusCode());

        if (this.includeContentType) {
            response.headers().get("Content-Type").peek(contentType ->
                logMessage.append(" [Content-Type: ").append(contentType).append("]")
            );
        }

        if (this.includeDuration) {
            logMessage.append(" [Duration: ").append(duration / 1_000_000).append(" ms]");
        }

        this.logger.info(logMessage.toString());
    }

    @Override
    public int order() {
        return ProcessingPriority.HIGHEST_PRECEDENCE - 512;
    }

    /**
     * Creates a new builder for a {@link RequestLoggingFilter}.
     *
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("includeClientInfo").writeBoolean(this.includeClientInfo);
        collector.property("includeQueryString").writeBoolean(this.includeQueryString);
        collector.property("includeContentType").writeBoolean(this.includeContentType);
        collector.property("includeResponse").writeBoolean(this.includeResponse);
        collector.property("includeDuration").writeBoolean(this.includeDuration);
    }

    /**
     * A builder for creating instances of {@link RequestLoggingFilter} with custom configurations.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class Builder {

        private boolean includeClientInfo = true;
        private boolean includeQueryString = false;
        private boolean includeContentType = true;
        private boolean includeResponse = true;
        private boolean includeDuration = false;

        /**
         * Sets whether to include the client remote address and port in the log messages.
         *
         * @param includeClientInfo Whether to include client information.
         * @return The builder instance.
         */
        public Builder includeClientInfo(boolean includeClientInfo) {
            this.includeClientInfo = includeClientInfo;
            return this;
        }

        /**
         * Sets whether to include the query string in the log messages.
         *
         * @param includeQueryString Whether to include the query string.
         * @return The builder instance.
         */
        public Builder includeQueryString(boolean includeQueryString) {
            this.includeQueryString = includeQueryString;
            return this;
        }

        /**
         * Sets whether to include the content type for both requests and responses in the log
         * messages.
         *
         * @param includeContentType Whether to include the content type.
         * @return The builder instance.
         */
        public Builder includeContentType(boolean includeContentType) {
            this.includeContentType = includeContentType;
            return this;
        }

        /**
         * Sets whether to log outgoing responses.
         *
         * @param includeResponse Whether to include response logging.
         * @return The builder instance.
         */
        public Builder includeResponse(boolean includeResponse) {
            this.includeResponse = includeResponse;
            return this;
        }

        /**
         * Sets whether to include the duration of request processing in the log messages.
         *
         * @param includeDuration Whether to include duration information.
         * @return The builder instance.
         */
        public Builder includeDuration(boolean includeDuration) {
            this.includeDuration = includeDuration;
            return this;
        }

        /**
         * Builds the {@link RequestLoggingFilter} with the configured options.
         *
         * @param logger The logger to use for logging messages.
         * @return A new instance of {@link RequestLoggingFilter}.
         */
        public RequestLoggingFilter build(Logger logger) {
            return new RequestLoggingFilter(
                    this.includeClientInfo,
                    this.includeQueryString,
                    this.includeContentType,
                    this.includeResponse,
                    this.includeDuration,
                    logger
            );
        }
    }
}
