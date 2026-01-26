package org.dockbox.hartshorn.web.chain;

import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.slf4j.Logger;

public class RequestLoggingFilter implements RequestFilter {

    private final boolean includeClientInfo;
    private final boolean includeQueryString;
    private final boolean includeContentType;
    private final boolean includeResponse;
    private final Logger logger;

    private RequestLoggingFilter(
            boolean includeClientInfo,
            boolean includeQueryString,
            boolean includeContentType,
            boolean includeResponse,
            Logger logger
    ) {
        this.includeClientInfo = includeClientInfo;
        this.includeQueryString = includeQueryString;
        this.includeContentType = includeContentType;
        this.includeResponse = includeResponse;
        this.logger = logger;
    }

    @Override
    public void handle(
            WebRequest request,
            WebResponse response,
            RequestFilterChain chain
    ) throws Exception {
        this.logInboundRequest(request);
        chain.accept(request, response);
        if (this.includeResponse) {
            this.logOutboundRequest(request, response);
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

    private void logOutboundRequest(WebRequest request, WebResponse response) {
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

        this.logger.info(logMessage.toString());
    }

    @Override
    public int order() {
        return ProcessingPriority.HIGHEST_PRECEDENCE - 512;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean includeClientInfo = true;
        private boolean includeQueryString = true;
        private boolean includeContentType = true;
        private boolean includeResponse = true;

        public Builder includeClientInfo(boolean includeClientInfo) {
            this.includeClientInfo = includeClientInfo;
            return this;
        }

        public Builder includeQueryString(boolean includeQueryString) {
            this.includeQueryString = includeQueryString;
            return this;
        }

        public Builder includeContentType(boolean includeContentType) {
            this.includeContentType = includeContentType;
            return this;
        }

        public Builder includeResponse(boolean includeResponse) {
            this.includeResponse = includeResponse;
            return this;
        }

        public RequestLoggingFilter build(Logger logger) {
            return new RequestLoggingFilter(
                    this.includeClientInfo,
                    this.includeQueryString,
                    this.includeContentType,
                    this.includeResponse,
                    logger
            );
        }
    }
}
