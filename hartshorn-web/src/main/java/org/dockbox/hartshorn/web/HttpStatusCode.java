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
 * Represents an HTTP status code, providing methods to retrieve the code and its series,
 * as well as utility methods to check the category of the status code.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HttpStatusCode {

    /**
     * Returns the numerical HTTP status code.
     *
     * @return the HTTP status code
     */
    int code();

    /**
     * Returns the series of the HTTP status code.
     *
     * @return the HTTP status series
     */
    HttpStatusSeries series();

    /**
     * Checks if the status code is informational (1xx).
     *
     * @return true if informational, false otherwise
     */
    default boolean isInformational() {
        return this.series() == HttpStatusSeries.INFORMATIONAL;
    }

    /**
     * Checks if the status code is successful (2xx).
     *
     * @return true if successful, false otherwise
     */
    default boolean isSuccessful() {
        return this.series() == HttpStatusSeries.SUCCESSFUL;
    }

    /**
     * Checks if the status code is a redirection (3xx).
     *
     * @return true if redirection, false otherwise
     */
    default boolean isRedirection() {
        return this.series() == HttpStatusSeries.REDIRECTION;
    }

    /**
     * Checks if the status code is a client error (4xx).
     *
     * @return true if client error, false otherwise
     */
    default boolean isClientError() {
        return this.series() == HttpStatusSeries.CLIENT_ERROR;
    }

    /**
     * Checks if the status code is a server error (5xx).
     *
     * @return true if server error, false otherwise
     */
    default boolean isServerError() {
        return this.series() == HttpStatusSeries.SERVER_ERROR;
    }

    /**
     * Checks if the status code is an error (4xx or 5xx).
     *
     * @return true if error, false otherwise
     */
    default boolean isError() {
        return this.isClientError() || this.isServerError();
    }

    /**
     * Creates an instance of {@link HttpStatusCode} for the given status code.
     * If the status code matches a predefined {@link HttpStatus}, that instance is returned.
     * Otherwise, a new instance is created with the appropriate series.
     *
     * @param statusCode the HTTP status code
     *
     * @return an instance of {@link HttpStatusCode}
     */
    static HttpStatusCode of(int statusCode) {
        HttpStatus status = HttpStatus.of(statusCode);
        if (status != null) {
            return status;
        }
        HttpStatusSeries series = HttpStatusSeries.of(statusCode);
        return new HttpStatusCode() {
            @Override
            public int code() {
                return statusCode;
            }

            @Override
            public HttpStatusSeries series() {
                return series;
            }
        };
    }
}
