package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletResponse;

/**
 * A {@link ResponseHandler} is responsible for translating the Object response of a request
 * handler. The response handler is responsible for writing the response, as well as setting any
 * necessary headers and status codes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ResponseHandler {

    /**
     * Handles the given result and writes it to the response. The implementation is responsible for
     * setting the appropriate content type and status code, if necessary.
     *
     * @param response the response to write the result to
     * @param result the result to write to the response
     * @throws Exception if an error occurs while writing the result to the response
     */
    void handleResponse(HttpServletResponse response, Object result) throws Exception;
}
