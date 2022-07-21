/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.util.ApplicationException;

/**
 * The exception thrown by module loaders accessing native functions. This exception is thrown
 * when a native function is called that is not supported by the module, or is not accessible
 * to the active runtime.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class NativeExecutionException extends ApplicationException {
    public NativeExecutionException(final String message, final Throwable e) {
        super(message, e);
    }

    public NativeExecutionException(final String message) {
        super(message);
    }
}
