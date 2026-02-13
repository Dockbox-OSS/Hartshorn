package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;

public interface TypeHttpMessageConverter<T> {

    boolean supports(Class<?> type);

    Option<T> read(Class<? extends T> type, HttpServletRequest request) throws Exception;

    void write(HttpServletResponse response, T result) throws Exception;
}
