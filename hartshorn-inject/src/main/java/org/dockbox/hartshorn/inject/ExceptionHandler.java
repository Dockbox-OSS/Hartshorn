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

package org.dockbox.hartshorn.inject;

/**
 * The exception handler is used to handle exceptions that occur during the application lifecycle.
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public interface ExceptionHandler {

    /**
     * Handles the given exception using the preferred behavior.
     *
     * @param throwable The exception to handle.
     */
    void handle(Throwable throwable);

    /**
     * Handles the given exception using the preferred behavior. The given message may be included to provide more
     * information about the exception.
     *
     * @param message The message to include in the exception.
     * @param throwable The exception to handle.
     */
    void handle(String message, Throwable throwable);

    /**
     * Whether to use stacktraces in the exception handler. This is useful for debugging purposes, but may be ignored
     * by the exception handler.
     *
     * @param stacktraces Whether to use stacktraces.
     * @return Itself, for chaining.
     */
    ExceptionHandler printStackTraces(boolean stacktraces);
}
