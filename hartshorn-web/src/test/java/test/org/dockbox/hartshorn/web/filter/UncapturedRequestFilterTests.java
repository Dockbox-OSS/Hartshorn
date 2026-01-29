package test.org.dockbox.hartshorn.web.filter;

import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.filter.RequestFilterChain;
import org.dockbox.hartshorn.web.filter.UncapturedRequestFilter;
import org.dockbox.hartshorn.web.message.MutableHttpMessageHeaders;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UncapturedRequestFilterTests {

    @Test
    void uncapturedRequestConfiguresResponse() throws Exception {
        WebRequest request = Mockito.mock(WebRequest.class);
        WebResponse response = Mockito.spy(WebResponse.class);
        Mockito.when(response.headers()).thenReturn(Mockito.mock(MutableHttpMessageHeaders.class));
        RequestFilterChain next = Mockito.spy(RequestFilterChain.class);

        UncapturedRequestFilter filter = new UncapturedRequestFilter();
        filter.handle(request, response, next);

        Mockito.verify(response).status(HttpStatus.NOT_FOUND);
        Mockito.verify(next, Mockito.never()).accept(request, response);
    }
}
