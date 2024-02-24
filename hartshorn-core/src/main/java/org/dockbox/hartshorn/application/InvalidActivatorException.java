/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

/**
 * Thrown when an attempt is made to register a type as service activator, but the type does not
 * meet the requirements of a service activator.
 *
 * @since 0.4.12
 *
 * @see org.dockbox.hartshorn.component.processing.ServiceActivator
 *
 * @author Guus Lieben
 */
public class InvalidActivatorException extends ApplicationRuntimeException {

    public InvalidActivatorException(String message) {
        super(message);
    }
}
