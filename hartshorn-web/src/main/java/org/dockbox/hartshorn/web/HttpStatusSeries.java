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

/**
 * Represents the series of HTTP status codes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public enum HttpStatusSeries {
    INFORMATIONAL,
    SUCCESSFUL,
    REDIRECTION,
    CLIENT_ERROR,
    SERVER_ERROR,
    UNKNOWN,
    ;

    /**
     * Returns the {@link HttpStatusSeries} corresponding to the given HTTP status code.
     *
     * @param code the HTTP status code
     * @return the corresponding HTTP status series
     */
    public static HttpStatusSeries of(int code) {
        int seriesCode = code / 100;
        return switch (seriesCode) {
            case 1 -> INFORMATIONAL;
            case 2 -> SUCCESSFUL;
            case 3 -> REDIRECTION;
            case 4 -> CLIENT_ERROR;
            case 5 -> SERVER_ERROR;
            default -> UNKNOWN;
        };
    }
}
