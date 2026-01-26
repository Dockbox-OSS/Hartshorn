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

import java.io.IOException;

/**
 * Represents the body of an HTTP message, providing methods to access its content.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HttpMessageBody {

    /**
     * Returns the content of the HTTP message body as a byte array. This requires reading the
     * entire body into memory.
     *
     * @return A byte array representing the content of the message body.
     *
     * @throws IOException If an I/O error occurs while reading the body.
     */
    byte[] bytes() throws IOException;

    /**
     * Returns the content of the HTTP message body as a string. This requires reading the entire
     * body into memory.
     *
     * @return A string representing the content of the message body.
     *
     * @throws IOException If an I/O error occurs while reading the body.
     */
    String asString() throws IOException;
}
