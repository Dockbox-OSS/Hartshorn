/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.discovery;

/**
 * Thrown when no implementation is available for a service. This may indicate the absence of a
 * service provider, or that the service provider is not able to provide an implementation.
 *
 * @see DiscoveryService
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class NoAvailableImplementationException extends ServiceDiscoveryException {

    public NoAvailableImplementationException(String message) {
        super(message);
    }

    public NoAvailableImplementationException(String message, Throwable cause) {
        super(message, cause);
    }
}
