package org.dockbox.hartshorn.web.message;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public class RequestAttributes {

    public static final String REQUEST_PATH_PARAMETERS = "hartshorn.web.request.path.parameters";

    public static Map<String, String> pathParameters(HttpServletRequest request) {
        Object attribute = request.getAttribute(REQUEST_PATH_PARAMETERS);
        if (attribute instanceof Map<?, ?> params) {
            @SuppressWarnings("unchecked")
            Map<String, String> stringParams = (Map<String, String>) params;
            return stringParams;
        }
        return Map.of();
    }

    public static void setPathParameters(HttpServletRequest request, Map<String, String> parameters) {
        request.setAttribute(REQUEST_PATH_PARAMETERS, parameters);
    }
}
