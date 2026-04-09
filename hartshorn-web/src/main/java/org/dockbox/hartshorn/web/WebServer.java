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
 * A simple web server interface that allows starting, stopping, and checking the status of the
 * server. While the server is running, it listens for incoming HTTP requests on a specified port.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface WebServer {

    /**
     * Starts the web server, allowing it to accept incoming HTTP requests. Web servers should start
     * in a non-blocking manner, allowing the application to continue initializing while the server
     * is starting up.
     *
     * @throws ServerException If an error occurs while starting the server.
     */
    void start() throws ServerException;

    /**
     * Stops the web server, preventing it from accepting further HTTP requests.
     *
     * @throws ServerException If an error occurs while stopping the server.
     */
    void stop() throws ServerException;

    /**
     * Checks if the web server is currently running.
     *
     * @return true if the server is running, false otherwise.
     */
    boolean running();

    /**
     * Returns the port number on which the web server is listening for incoming HTTP requests.
     *
     * @return The port number of the web server.
     */
    int port();
}
