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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Thrown when no injectable constructors are found for a given type.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class MissingInjectConstructorException extends ApplicationException {
    public MissingInjectConstructorException(TypeView<?> type) {
        super("No injectable constructors found for " + type.name());
    }

    public MissingInjectConstructorException(TypeView<?> type, Throwable throwable) {
        super("No injectable constructors found for " + type.name(), throwable);
    }
}
