/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.error;

import org.dockbox.hartshorn.util.option.Option;

/**
 * A registry for {@link RequestErrorHandler} instances. This registry is used to register and
 * retrieve error handlers for specific {@link Exception} types. If there is no exact match for a
 * given exception type, the nearest compatible handler is used instead (if any).
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ErrorHandlerRegistry {

    /**
     * Register a new error handler for the given exception type.
     *
     * @param exceptionType the exception type to register the handler for
     * @param handler the error handler to register
     * @param <E> the type of the exception
     */
    <E extends Throwable> void register(
            Class<E> exceptionType,
            RequestErrorHandler<E> handler
    );

    /**
     * Retrieve the error handler for the given exception type. If there is no exact match, the
     * nearest compatible handler is used instead (if any).
     *
     * @param exceptionType the exception type to retrieve the handler for
     * @return an {@link Option} containing the error handler, or an empty {@link Option} if no
     * handler is found
     */
    Option<RequestErrorHandler<? extends Throwable>> getHandler(
            Class<? extends Throwable> exceptionType
    );
}
