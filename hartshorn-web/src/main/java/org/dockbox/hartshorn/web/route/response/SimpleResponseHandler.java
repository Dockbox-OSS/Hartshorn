package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.util.List;

/**
 * A simple implementation of {@link ResponseHandler} that uses a list of typed
 * {@link HttpMessageConverter}s to convert specific types of responses, and a single
 * {@link GenericHttpMessageConverter} to convert any uncaptured response types.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleResponseHandler implements ResponseHandler {

    private final GenericHttpMessageConverter defaultConverter;
    private final List<HttpMessageConverter<?>> converters;

    public SimpleResponseHandler(
            GenericHttpMessageConverter defaultConverter,
            List<HttpMessageConverter<?>> converters
    ) {
        this.defaultConverter = defaultConverter;
        this.converters = converters;
    }

    @Override
    public void handleResponse(HttpServletResponse response, Object result) throws Exception {
        if (result == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        List<HttpMessageConverter<?>> compatibleConverters = this.converters.stream()
                .filter(converter -> converter.supports(result.getClass()))
                .toList();
        if (compatibleConverters.isEmpty()) {
            this.defaultConverter.write(response, result);
        }
        else if (compatibleConverters.size() == 1) {
            compatibleConverters.getFirst().write(
                    response,
                    TypeUtils.unchecked(result, Object.class)
            );
        }
        else {
            throw new IllegalStateException(
                    "Multiple compatible converters found for type " + result.getClass()
            );
        }
    }
}
