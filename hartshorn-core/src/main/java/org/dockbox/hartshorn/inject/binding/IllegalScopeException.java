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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Thrown when a scope is not valid for a given binding. For example, when a singleton scope is installed
 * on a binding that does not allow for singletons.
 *
 * @author Guus Lieben
 * @since 0.5.0
 *
 * @see BindingFunction
 */
public class IllegalScopeException extends ApplicationException {

    public IllegalScopeException(String message) {
        super(message);
    }

    public IllegalScopeException(String message, Throwable cause) {
        super(message, cause);
    }
}
