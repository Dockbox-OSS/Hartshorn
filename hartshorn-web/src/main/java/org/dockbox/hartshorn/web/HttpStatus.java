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

package org.dockbox.hartshorn.web;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Enumeration of HTTP status codes as defined in RFC 7231 and other related RFCs.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6">RFC 7231, Section 6</a>
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public enum HttpStatus implements HttpStatusCode {
    // checkstyle:off LineLength

    // 1xx Informational
    CONTINUE(HttpServletResponse.SC_CONTINUE, "Continue"),
    SWITCHING_PROTOCOLS(HttpServletResponse.SC_SWITCHING_PROTOCOLS, "Switching Protocols"),
    PROCESSING(102, "Processing"),
    EARLY_HINTS(103, "Early Hints"),

    // 2xx Successful
    OK(HttpServletResponse.SC_OK, "OK"),
    CREATED(HttpServletResponse.SC_CREATED, "Created"),
    ACCEPTED(HttpServletResponse.SC_ACCEPTED, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION, "Non-Authoritative Information"),
    NO_CONTENT(HttpServletResponse.SC_NO_CONTENT, "No Content"),
    RESET_CONTENT(HttpServletResponse.SC_RESET_CONTENT, "Reset Content"),
    PARTIAL_CONTENT(HttpServletResponse.SC_PARTIAL_CONTENT, "Partial Content"),
    MULTI_STATUS(207, "Multi-Status"),
    ALREADY_REPORTED(208, "Already Reported"),
    IM_USED(226, "IM Used"),

    // 3xx Redirection
    MULTIPLE_CHOICES(HttpServletResponse.SC_MULTIPLE_CHOICES, "Multiple Choices"),
    MOVED_PERMANENTLY(HttpServletResponse.SC_MOVED_PERMANENTLY, "Moved Permanently"),
    FOUND(HttpServletResponse.SC_FOUND, "Found"),
    SEE_OTHER(HttpServletResponse.SC_SEE_OTHER, "See Other"),
    NOT_MODIFIED(HttpServletResponse.SC_NOT_MODIFIED, "Not Modified"),
    USE_PROXY(HttpServletResponse.SC_USE_PROXY, "Use Proxy"),
    TEMPORARY_REDIRECT(HttpServletResponse.SC_TEMPORARY_REDIRECT, "Temporary Redirect"),
    PERMANENT_REDIRECT(HttpServletResponse.SC_PERMANENT_REDIRECT, "Permanent Redirect"),

    // 4xx Client Error
    BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST, "Bad Request"),
    UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"),
    PAYMENT_REQUIRED(HttpServletResponse.SC_PAYMENT_REQUIRED, "Payment Required"),
    FORBIDDEN(HttpServletResponse.SC_FORBIDDEN, "Forbidden"),
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "Not Found"),
    METHOD_NOT_ALLOWED(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed"),
    NOT_ACCEPTABLE(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(HttpServletResponse.SC_REQUEST_TIMEOUT, "Request Timeout"),
    CONFLICT(HttpServletResponse.SC_CONFLICT, "Conflict"),
    GONE(HttpServletResponse.SC_GONE, "Gone"),
    LENGTH_REQUIRED(HttpServletResponse.SC_LENGTH_REQUIRED, "Length Required"),
    PRECONDITION_FAILED(HttpServletResponse.SC_PRECONDITION_FAILED, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "Payload Too Large"),
    URI_TOO_LONG(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type"),
    RANGE_NOT_SATISFIABLE(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE, "Range Not Satisfiable"),
    EXPECTATION_FAILED(HttpServletResponse.SC_EXPECTATION_FAILED, "Expectation Failed"),
    IM_A_TEAPOT(418, "I'm a teapot"),
    MISDIRECTED_REQUEST(HttpServletResponse.SC_MISDIRECTED_REQUEST, "Misdirected Request"),
    UNPROCESSABLE_ENTITY(HttpServletResponse.SC_UNPROCESSABLE_CONTENT, "Unprocessable Entity"),
    LOCKED(423, "Locked"),
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    TOO_EARLY(425, "Too Early"),
    UPGRADE_REQUIRED(HttpServletResponse.SC_UPGRADE_REQUIRED, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),

    // 5xx Server Error
    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error"),
    NOT_IMPLEMENTED(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not Implemented"),
    BAD_GATEWAY(HttpServletResponse.SC_BAD_GATEWAY, "Bad Gateway"),
    SERVICE_UNAVAILABLE(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service Unavailable"),
    GATEWAY_TIMEOUT(HttpServletResponse.SC_GATEWAY_TIMEOUT, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED, "HTTP Version Not Supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    LOOP_DETECTED(508, "Loop Detected"),
    NOT_EXTENDED(510, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required"),

    ;
    // checkstyle:on LineLength

    private final int code;
    private final String reasonPhrase;
    private final HttpStatusSeries series;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
        this.series = HttpStatusSeries.of(code);
    }

    /**
     * Returns the reason phrase of this HTTP status code.
     *
     * @return the reason phrase
     */
    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public HttpStatusSeries series() {
        return this.series;
    }

    /**
     * Returns the {@link HttpStatus} corresponding to the given status code. If no matching
     * status code is found, {@code null} is returned.
     *
     * @param status the HTTP status code
     *
     * @return the corresponding {@link HttpStatus}, or {@code null} if not found
     */
    public static HttpStatus of(int status) {
        for (HttpStatus httpStatus : values()) {
            if (httpStatus.code == status) {
                return httpStatus;
            }
        }
        return null;
    }
}
