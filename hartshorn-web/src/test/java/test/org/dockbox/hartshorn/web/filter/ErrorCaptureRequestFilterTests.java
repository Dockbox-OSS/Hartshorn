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

package test.org.dockbox.hartshorn.web.filter;

import org.dockbox.hartshorn.inject.ExceptionHandler;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.filter.ErrorCaptureRequestFilter;
import org.dockbox.hartshorn.web.filter.RequestFilterChain;
import org.dockbox.hartshorn.web.message.MutableHttpMessageHeaders;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ErrorCaptureRequestFilterTests {

    @Test
    void exceptionsThrownUpstreamAreCaptured() throws Exception {
        ExceptionHandler exceptionHandler = Mockito.spy(ExceptionHandler.class);
        ErrorCaptureRequestFilter filter = new ErrorCaptureRequestFilter(exceptionHandler);

        WebRequest request = Mockito.mock(WebRequest.class);
        WebResponse response = Mockito.spy(WebResponse.class);
        Mockito.when(response.headers()).thenReturn(Mockito.mock(MutableHttpMessageHeaders.class));

        RequestFilterChain next = (_, _) -> {
            throw new RuntimeException("Test exception");
        };
        filter.handle(request, response, next);

        Mockito.verify(exceptionHandler).handle(
                Mockito.eq("An error occurred while processing a web request"),
                Mockito.any(RuntimeException.class)
        );
        Mockito.verify(response).status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void effectivelyNoOpWhenNoUpstreamExceptionIsThrown() throws Exception {
        ExceptionHandler exceptionHandler = Mockito.spy(ExceptionHandler.class);
        ErrorCaptureRequestFilter filter = new ErrorCaptureRequestFilter(exceptionHandler);

        WebRequest request = Mockito.mock(WebRequest.class);
        WebResponse response = Mockito.spy(WebResponse.class);
        Mockito.when(response.headers()).thenReturn(Mockito.mock(MutableHttpMessageHeaders.class));

        RequestFilterChain next = (_, _) -> {
            // No exception thrown
            return true;
        };
        filter.handle(request, response, next);

        Mockito.verifyNoInteractions(exceptionHandler);
        Mockito.verify(response, Mockito.never()).status(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
