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

package org.dockbox.hartshorn.util.introspect.scan;

import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Thrown when a {@link TypeReference} cannot be loaded.
 *
 * @see TypeReference#getOrLoad()
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ClassReferenceLoadException extends ApplicationException {

    public ClassReferenceLoadException(String message) {
        super(message);
    }

    public ClassReferenceLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassReferenceLoadException(Throwable cause) {
        super(cause);
    }
}
