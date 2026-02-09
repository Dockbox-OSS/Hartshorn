package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletResponse;

public interface ResponseHandler {

    void handleResponse(HttpServletResponse response, Object result) throws Exception;
}
