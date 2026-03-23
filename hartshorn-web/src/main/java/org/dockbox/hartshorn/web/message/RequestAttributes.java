package org.dockbox.hartshorn.web.message;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * Utility class describing common attributes of {@link HttpServletRequest servlet requests} within
 * Hartshorn Web.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class RequestAttributes {

    public static final String REQUEST_PATH_PARAMETERS = "hartshorn.web.request.path.parameters";

    /**
     * Collects any path parameters defined for the given request. If the attribute was not set, an
     * empty {@link Map} is returned instead.
     *
     * @param request the request to collect path parameters from
     * @return any defined path parameters, or an empty {@link Map} if none were defined
     */
    public static Map<String, String> pathParameters(HttpServletRequest request) {
        Object attribute = request.getAttribute(REQUEST_PATH_PARAMETERS);
        if (attribute instanceof Map<?, ?> params) {
            @SuppressWarnings("unchecked")
            Map<String, String> stringParams = (Map<String, String>) params;
            return stringParams;
        }
        return Map.of();
    }

    /**
     * Adds the given request parameters to the given request under the
     * {@value #REQUEST_PATH_PARAMETERS} attribute.
     *
     * @param request the request to add the attribute to
     * @param parameters the parameters to set as value for the attribute
     */
    public static void setPathParameters(
            HttpServletRequest request,
            Map<String, String> parameters
    ) {
        request.setAttribute(REQUEST_PATH_PARAMETERS, parameters);
    }
}
