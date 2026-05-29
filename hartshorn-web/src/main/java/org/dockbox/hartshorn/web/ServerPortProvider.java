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
 * Provides the port number on which the web server should run. This interface can be implemented
 * to allow for dynamic port configuration, such as reading from environment variables or
 * configuration files.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ServerPortProvider {

    /**
     * Constant indicating that the port selection should remain dynamic, allowing the web server to
     * select an available port at runtime.
     */
    int DYNAMIC_SELECTION = -1;

    /**
     * Returns the port number on which the web server should run, or {@value #DYNAMIC_SELECTION} if
     * port selection should remain dynamic.
     *
     * @return the port number on which the web server should run, or {@value DYNAMIC_SELECTION}
     */
    int port();
}
