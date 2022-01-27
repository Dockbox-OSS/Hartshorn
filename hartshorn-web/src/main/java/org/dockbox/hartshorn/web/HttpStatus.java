/*
 * Copyright 2019-2022 the original author or authors.
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

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpStatus {

    // 1xx Informational
    CONTINUE(100, Series.INFORMATIONAL, "Continue"),
    SWITCHING_PROTOCOLS(101, Series.INFORMATIONAL, "Switching Protocols"),
    PROCESSING(102, Series.INFORMATIONAL, "Processing"),
    CHECKPOINT(103, Series.INFORMATIONAL, "Checkpoint"),

    // 2xx Success
    OK(200, Series.SUCCESSFUL, "OK"),
    CREATED(201, Series.SUCCESSFUL, "Created"),
    ACCEPTED(202, Series.SUCCESSFUL, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, Series.SUCCESSFUL, "Non-Authoritative Information"),
    NO_CONTENT(204, Series.SUCCESSFUL, "No Content"),
    RESET_CONTENT(205, Series.SUCCESSFUL, "Reset Content"),
    PARTIAL_CONTENT(206, Series.SUCCESSFUL, "Partial Content"),
    MULTI_STATUS(207, Series.SUCCESSFUL, "Multi-Status"),
    ALREADY_REPORTED(208, Series.SUCCESSFUL, "Already Reported"),
    IM_USED(226, Series.SUCCESSFUL, "IM Used"),

    // 3xx Redirection
    MULTIPLE_CHOICES(300, Series.REDIRECTION, "Multiple Choices"),
    MOVED_PERMANENTLY(301, Series.REDIRECTION, "Moved Permanently"),
    FOUND(302, Series.REDIRECTION, "Found"),
    MOVED_TEMPORARILY(302, Series.REDIRECTION, "Moved Temporarily"),
    SEE_OTHER(303, Series.REDIRECTION, "See Other"),
    NOT_MODIFIED(304, Series.REDIRECTION, "Not Modified"),
    USE_PROXY(305, Series.REDIRECTION, "Use Proxy"),
    TEMPORARY_REDIRECT(307, Series.REDIRECTION, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, Series.REDIRECTION, "Permanent Redirect"),

    // --- 4xx Client Error ---
    BAD_REQUEST(400, Series.CLIENT_ERROR, "Bad Request"),
    UNAUTHORIZED(401, Series.CLIENT_ERROR, "Unauthorized"),
    PAYMENT_REQUIRED(402, Series.CLIENT_ERROR, "Payment Required"),
    FORBIDDEN(403, Series.CLIENT_ERROR, "Forbidden"),
    NOT_FOUND(404, Series.CLIENT_ERROR, "Not Found"),
    METHOD_NOT_ALLOWED(405, Series.CLIENT_ERROR, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, Series.CLIENT_ERROR, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, Series.CLIENT_ERROR, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, Series.CLIENT_ERROR, "Request Timeout"),
    CONFLICT(409, Series.CLIENT_ERROR, "Conflict"),
    GONE(410, Series.CLIENT_ERROR, "Gone"),
    LENGTH_REQUIRED(411, Series.CLIENT_ERROR, "Length Required"),
    PRECONDITION_FAILED(412, Series.CLIENT_ERROR, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, Series.CLIENT_ERROR, "Payload Too Large"),
    REQUEST_ENTITY_TOO_LARGE(413, Series.CLIENT_ERROR, "Request Entity Too Large"),
    URI_TOO_LONG(414, Series.CLIENT_ERROR, "URI Too Long"),
    REQUEST_URI_TOO_LONG(414, Series.CLIENT_ERROR, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, Series.CLIENT_ERROR, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, Series.CLIENT_ERROR, "Requested range not satisfiable"),
    EXPECTATION_FAILED(417, Series.CLIENT_ERROR, "Expectation Failed"),
    I_AM_A_TEAPOT(418, Series.CLIENT_ERROR, "I'm a teapot"),
    INSUFFICIENT_SPACE_ON_RESOURCE(419, Series.CLIENT_ERROR, "Insufficient Space On Resource"),
    METHOD_FAILURE(420, Series.CLIENT_ERROR, "Method Failure"),
    DESTINATION_LOCKED(421, Series.CLIENT_ERROR, "Destination Locked"),
    UNPROCESSABLE_ENTITY(422, Series.CLIENT_ERROR, "Unprocessable Entity"),
    LOCKED(423, Series.CLIENT_ERROR, "Locked"),
    FAILED_DEPENDENCY(424, Series.CLIENT_ERROR, "Failed Dependency"),
    TOO_EARLY(425, Series.CLIENT_ERROR, "Too Early"),
    UPGRADE_REQUIRED(426, Series.CLIENT_ERROR, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, Series.CLIENT_ERROR, "Precondition Required"),
    TOO_MANY_REQUESTS(429, Series.CLIENT_ERROR, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, Series.CLIENT_ERROR, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, Series.CLIENT_ERROR, "Unavailable For Legal Reasons"),

    // --- 5xx Server Error ---
    INTERNAL_SERVER_ERROR(500, Series.SERVER_ERROR, "Internal Server Error"),
    NOT_IMPLEMENTED(501, Series.SERVER_ERROR, "Not Implemented"),
    BAD_GATEWAY(502, Series.SERVER_ERROR, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, Series.SERVER_ERROR, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, Series.SERVER_ERROR, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, Series.SERVER_ERROR, "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES(506, Series.SERVER_ERROR, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, Series.SERVER_ERROR, "Insufficient Storage"),
    LOOP_DETECTED(508, Series.SERVER_ERROR, "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED(509, Series.SERVER_ERROR, "Bandwidth Limit Exceeded"),
    NOT_EXTENDED(510, Series.SERVER_ERROR, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, Series.SERVER_ERROR, "Network Authentication Required");

    private static final HttpStatus[] VALUES;

    static {
        VALUES = values();
    }

    private final int value;
    private final Series series;
    private final String message;

    public boolean isError() {
        return (this.series() == Series.CLIENT_ERROR || this.series() == Series.SERVER_ERROR);
    }

    @Override
    public String toString() {
        return this.value + " " + this.name();
    }

    public static HttpStatus of(final int statusCode) {
        for (final HttpStatus status : VALUES) {
            if (status.value == statusCode) return status;
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    public enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        public static final Series[] VALUES = values();
        private final int value;

        public static Series resolve(final int statusCode) {
            final int seriesCode = statusCode / 100;
            for (final Series series : Series.VALUES) {
                if (series.value == seriesCode) return series;
            }
            return null;
        }
    }
}