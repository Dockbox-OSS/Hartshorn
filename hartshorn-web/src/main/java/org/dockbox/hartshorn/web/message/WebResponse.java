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

package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.web.HttpStatusCode;

import java.nio.ByteBuffer;

/**
 * Represents a web response, providing methods to write data, set status codes, and manage headers.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface WebResponse {

    /**
     * Writes the given data to the response.
     *
     * @param data The data to write.
     * @throws Exception If an error occurs while writing the data.
     */
    void write(ByteBuffer data) throws Exception;

    /**
     * Writes the given body to the response using the provided writer.
     *
     * @param <T> The type of the body to write.
     * @param writer The writer to use for writing the body.
     * @param body The body to write.
     * @throws Exception If an error occurs while writing the body.
     */
    <T> void write(ResponseWriter<T> writer, T body) throws Exception;

    /**
     * Sets the status code of the response.
     *
     * @param status The status code to set.
     */
    void status(HttpStatusCode status);

    /**
     * Returns the status code of the response.
     *
     * @return The status code.
     */
    HttpStatusCode statusCode();

    /**
     * Returns the mutable headers of the response.
     *
     * @return The mutable HTTP message headers.
     */
    MutableHttpMessageHeaders headers();

    /**
     * Indicates whether the response has been committed.
     *
     * @return true if the response is committed, false otherwise.
     */
    boolean committed();
}
