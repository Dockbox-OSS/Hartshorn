package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;

public interface GlobalResponseMessageConverter {

    Option<?> read(Class<?> type, HttpServletRequest request) throws Exception;

    void write(HttpServletResponse response, Object result) throws Exception;
}
