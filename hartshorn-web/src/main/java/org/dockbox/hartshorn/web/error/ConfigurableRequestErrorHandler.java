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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.dockbox.hartshorn.util.types.TypeUtils;

/**
 * A {@link RequestErrorHandler} that delegates to exception-specific {@link RequestErrorHandler}s
 * defined by the configured {@link ErrorHandlerRegistry}. For exceptions that do not match any
 * handler in the registry, the provided default handler is used instead.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ConfigurableRequestErrorHandler implements RequestErrorHandler<Throwable> {

    private final ErrorHandlerRegistry registry;
    private final RequestErrorHandler<Throwable> defaultHandler;

    public ConfigurableRequestErrorHandler(
            ErrorHandlerRegistry registry, RequestErrorHandler<Throwable> defaultHandler) {
        this.registry = registry;
        this.defaultHandler = defaultHandler;
    }

    @Override
    public void handle(Throwable exception, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        this.registry
                .getHandler(exception.getClass())
                .orElse(this.defaultHandler)
                .handle(TypeUtils.unchecked(exception, Throwable.class), request, response);
    }
}
