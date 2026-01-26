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

import org.dockbox.hartshorn.util.option.Option;

/**
 * An enumeration of HTTP methods.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public enum HttpMethod {
    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-4.3.1">
     *      RFC 7231, Section 4.3.1
     *      </a>
     */
    GET,
    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-4.3.2">
     *      RFC 7231, Section 4.3.2
     *      </a>
     */
    HEAD,
    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-4.3.3">
     *      RFC 7231, Section 4.3.3
     *      </a>
     */
    POST,
    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-4.3.4">
     *      RFC 7231, Section 4.3.4
     *      </a>
     */
    PUT,
    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-4.3.5">
     *      RFC 7231, Section 4.3.5
     *      </a>
     */
    DELETE,
    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-4.3.6">
     *      RFC 7231, Section 4.3.6
     *      </a>
     */
    CONNECT,
    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-4.3.7">
     *      RFC 7231, Section 4.3.7
     *      </a>
     */
    OPTIONS,
    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-4.3.8">
     *      RFC 7231, Section 4.3.8
     *      </a>
     */
    TRACE,
    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc5789">
     *      RFC 5789, Section 2
     *      </a>
     */
    PATCH,
    ;

    /**
     * Converts a string representation of an HTTP method to its corresponding {@link HttpMethod}
     * enum value.
     *
     * @param method The string representation of the HTTP method.
     *
     * @return An {@link Option} containing the corresponding {@link HttpMethod} if found,
     * otherwise an empty {@link Option}.
     */
    public static Option<HttpMethod> fromString(String method) {
        for (HttpMethod httpMethod : values()) {
            if (httpMethod.name().equalsIgnoreCase(method)) {
                return Option.of(httpMethod);
            }
        }
        return Option.empty();
    }
}
