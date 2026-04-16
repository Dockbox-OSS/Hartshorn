package org.dockbox.hartshorn.web.response.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.response.HttpMessageConverter;

public class NoContentMessageConverter implements HttpMessageConverter<Object> {

    @Override
    public boolean supportsRequest(Class<?> type, HttpServletRequest request) {
        return false;
    }

    @Override
    public boolean supportsResponse(Class<?> type, HttpServletRequest request, HttpServletResponse response) {
        return type == null;
    }

    @Override
    public Option<Object> read(Class<?> type, HttpServletRequest request) {
        return Option.empty();
    }

    @Override
    public void write(HttpServletResponse response, Object result) {
        response.setStatus(HttpStatus.NO_CONTENT.code());
    }
}
