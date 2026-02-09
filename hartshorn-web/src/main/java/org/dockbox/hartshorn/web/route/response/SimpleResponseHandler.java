package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.util.List;

public class SimpleResponseHandler implements ResponseHandler {

    private final GlobalResponseMessageConverter defaultConverter;
    private final List<ResponseMessageConverter<?>> converters;

    public SimpleResponseHandler(
            GlobalResponseMessageConverter defaultConverter,
            List<ResponseMessageConverter<?>> converters
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
        List<ResponseMessageConverter<?>> compatibleConverters = this.converters.stream()
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
