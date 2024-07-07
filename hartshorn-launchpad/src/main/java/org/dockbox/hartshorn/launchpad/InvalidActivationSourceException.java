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

package org.dockbox.hartshorn.launchpad;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

/**
 * An exception that is thrown when an attempt is made to use an invalid activator class
 * for a new {@link ApplicationContext}. Typically, a class is considered valid when it
 * is a class that is not:
 * <ul>
 *     <li>abstract</li>
 *     <li>an interface</li>
 *     <li>an array</li>
 *     <li>a primitive</li>
 *     <li>a local class</li>
 *     <li>a member class</li>
 *     <li>in a reserved package</li>
 * </ul>
 *
 * <p>Note that specific {@link org.dockbox.hartshorn.application.ApplicationBuilder} implementations
 * may have additional requirements for valid activator classes.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class InvalidActivationSourceException extends ApplicationRuntimeException {

    public InvalidActivationSourceException(String message) {
        super(message);
    }
}
