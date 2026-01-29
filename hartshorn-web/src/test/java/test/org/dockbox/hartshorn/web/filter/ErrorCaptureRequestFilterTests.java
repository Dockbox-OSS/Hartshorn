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
