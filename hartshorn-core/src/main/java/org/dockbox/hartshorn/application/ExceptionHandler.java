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

package org.dockbox.hartshorn.application;

/**
 * The exception handler is used to handle exceptions that occur during the application lifecycle.
 *
 * @author Guus Lieben
 * @since 21.9
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
    ExceptionHandler stacktraces(boolean stacktraces);

    /**
     * Throw the provided {@link Throwable} as if it were unchecked. This treats the exception as if it were unchecked,
     * and will not require it to be handled by javac. This is possible because of a side effect of type erasure, see
     * the link below for more details. The return result is only used to mimic a return value when it is needed, as the
     * {@code throws} cause is not enough to make the compiler know that the method will throw an exception.
     *
     * @param t The exception to throw.
     * @param <T> The type of the exception.
     * @throws T The exception to throw.
     * @see <a href="https://blog.jooq.org/throw-checked-exceptions-like-runtime-exceptions-in-java/">Throw checked exceptions like runtime exceptions in Java</a>
     */
    static <T extends Throwable, R> R unchecked(final Throwable t) throws T {
        throw (T) t;
    }
}
