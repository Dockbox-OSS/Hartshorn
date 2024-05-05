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

package org.dockbox.hartshorn.hsl.objects;

import org.dockbox.hartshorn.util.ApplicationException;

/**
 * The exception thrown by module loaders accessing native functions. This exception is thrown
 * when a native function is called that is not supported by the module, or is not accessible
 * to the active runtime.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class NativeExecutionException extends ApplicationException {
    public NativeExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NativeExecutionException(String message) {
        super(message);
    }
}
