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

package test.org.dockbox.hartshorn.web.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.WebServerConfiguration;
import org.dockbox.hartshorn.web.error.ConfigurableRequestErrorHandler;
import org.dockbox.hartshorn.web.error.RequestErrorHandler;
import org.dockbox.hartshorn.web.error.SimpleErrorHandlerRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorHandlerTests {

    @Test
    void exactHandlerIsPreferredOverParentHandler() {
        SimpleErrorHandlerRegistry registry = new SimpleErrorHandlerRegistry();
        RequestErrorHandler<Exception> parentHandler = (_, _, _) -> {};
        RequestErrorHandler<IllegalArgumentException> exactHandler =
                (_, _, _) -> {};
        registry.register(Exception.class, parentHandler);
        registry.register(IllegalArgumentException.class, exactHandler);

        Assertions.assertThat(registry.getHandler(IllegalArgumentException.class).get())
                .isSameAs(exactHandler);
    }

    @Test
    void nearestParentHandlerIsSelectedWhenNoExactHandlerExists() {
        SimpleErrorHandlerRegistry registry = new SimpleErrorHandlerRegistry();
        RequestErrorHandler<Throwable> broadHandler = (_, _, _) -> {};
        RequestErrorHandler<RuntimeException> nearestHandler =
                (_, _, _) -> {};
        registry.register(Throwable.class, broadHandler);
        registry.register(RuntimeException.class, nearestHandler);

        Assertions.assertThat(registry.getHandler(IllegalArgumentException.class).get())
                .isSameAs(nearestHandler);
    }

    @Test
    void missingHandlerIsReportedAsAbsent() {
        SimpleErrorHandlerRegistry registry = new SimpleErrorHandlerRegistry();

        Assertions.assertThat(registry.getHandler(IllegalArgumentException.class).absent())
                .isTrue();
    }

    @Test
    void configurableHandlerDelegatesToRegisteredHandler() throws IOException {
        SimpleErrorHandlerRegistry registry = new SimpleErrorHandlerRegistry();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        IllegalStateException exception = new IllegalStateException("request failed");
        RequestErrorHandler<IllegalStateException> registeredHandler =
                (actualException, actualRequest, actualResponse) -> {
                    Assertions.assertThat(actualException).isSameAs(exception);
                    Assertions.assertThat(actualRequest).isSameAs(request);
                    Assertions.assertThat(actualResponse).isSameAs(response);
                };
        registry.register(IllegalStateException.class, registeredHandler);
        ConfigurableRequestErrorHandler handler = new ConfigurableRequestErrorHandler(
                registry,
                (_, _, _) -> {
                    // Default fallback behavior
                    throw new AssertionError("Expected the registered handler to be used");
                }
        );

        handler.handle(exception, request, response);
    }

    @Test
    void defaultHandlerWritesInternalServerErrorResponse() throws IOException {
        SimpleErrorHandlerRegistry registry = new SimpleErrorHandlerRegistry();
        RequestErrorHandler<Throwable> handler = new WebServerConfiguration()
                .defaultRequestErrorHandler(registry);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(body));

        handler.handle(new IllegalStateException("request failed"), request, response);

        Mockito.verify(response).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.code());
        Mockito.verify(response).flushBuffer();
        Assertions.assertThat(body).hasToString("Internal Server Error: request failed");
    }

    @Test
    void configurableHandlerPropagatesIoFailures() {
        IOException failure = new IOException("response failed");
        ConfigurableRequestErrorHandler handler = new ConfigurableRequestErrorHandler(
                new SimpleErrorHandlerRegistry(),
                (_, _, _) -> {
                    throw failure;
                }
        );

        Assertions.assertThatThrownBy(() -> handler.handle(
                        new IllegalStateException("request failed"),
                        Mockito.mock(HttpServletRequest.class),
                        Mockito.mock(HttpServletResponse.class)
                ))
                .isSameAs(failure);
    }
}