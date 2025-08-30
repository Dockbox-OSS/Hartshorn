/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Exception to indicate that a type parameter could not be resolved. This can occur when the type parameter is
 * not available at runtime, such as when using raw types or when the type parameter is erased due to type erasure in
 * Java generics.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class TypeParameterResolutionException extends ApplicationException {

    public TypeParameterResolutionException(String message) {
        super(message);
    }

    public TypeParameterResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
