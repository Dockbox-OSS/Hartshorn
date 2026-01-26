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

/**
 * Represents a client making a web request, providing access to its address and port.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface WebRequestClient {

    /**
     * Returns the address of the client.
     *
     * @return The client's address as a string.
     */
    String address();

    /**
     * Returns the port of the client.
     *
     * @return The client's port as an integer.
     */
    int port();
}
