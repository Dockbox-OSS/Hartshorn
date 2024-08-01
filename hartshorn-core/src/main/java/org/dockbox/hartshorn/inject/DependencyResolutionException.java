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

/**
 * Thrown by a {@link DependencyResolver} when a dependency cannot be resolved. This is usually
 * caused by a missing injectable constructor, or an invalid dependency declaration.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class DependencyResolutionException extends ApplicationException {

    public DependencyResolutionException(String message) {
        super(message);
    }

    public DependencyResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DependencyResolutionException(Throwable cause) {
        super(cause);
    }
}
