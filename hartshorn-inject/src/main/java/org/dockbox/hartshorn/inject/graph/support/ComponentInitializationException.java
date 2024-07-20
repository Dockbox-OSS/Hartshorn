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

package org.dockbox.hartshorn.inject.graph.support;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

/**
 * Thrown when a component cannot be initialized. This is usually caused by a missing dependency,
 * or an exception thrown during the initialization of a component.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ComponentInitializationException extends ApplicationRuntimeException {

    public ComponentInitializationException(String message) {
        super(message);
    }

    public ComponentInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
